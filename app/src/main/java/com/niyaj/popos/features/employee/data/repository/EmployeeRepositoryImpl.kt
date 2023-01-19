package com.niyaj.popos.features.employee.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class EmployeeRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EmployeeRepository, EmployeeValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Employee Session")
    }

    override suspend fun getAllEmployee(): Flow<Resource<List<Employee>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val employees = realm.query<Employee>().sort("employeeId", Sort.DESCENDING).find()

                    val items = employees.asFlow()

                    items.collect { changes: ResultsChange<Employee> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get employee items", emptyList()))
                }
            }
        }
    }

    override fun getEmployeeById(employeeId: String): Resource<Employee?> {
        return try {
            val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()


            Resource.Success(employee)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Employee", null)
        }
    }

    override fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean {
        val employee = if (employeeId == null) {
            realm.query<Employee>("employeeName == $0", employeeName).first().find()
        } else {
            realm.query<Employee>(
                "employeeId != $0 && employeeName == $1",
                employeeId,
                employeeName
            ).first().find()
        }

        return employee != null
    }

    override fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean {
        val employee = if (employeeId == null) {
            realm.query<Employee>("employeePhone == $0", employeePhone).first().find()
        } else {
            realm.query<Employee>(
                "employeeId != $0 && employeePhone == $1",
                employeeId,
                employeePhone
            ).first().find()
        }

        return employee != null
    }

    override suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
            val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone)
            val validateEmployeePosition = validateEmployeePosition(newEmployee.employeePosition)
            val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

            val hasError = listOf(validateEmployeeName, validateEmployeePhone, validateEmployeePosition, validateEmployeeSalary).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val employee = Employee()
                    employee.employeeId = newEmployee.employeeId.ifEmpty { BsonObjectId().toHexString() }
                    employee.employeeName = newEmployee.employeeName
                    employee.employeePhone = newEmployee.employeePhone
                    employee.employeeType = newEmployee.employeeType
                    employee.employeeSalary = newEmployee.employeeSalary
                    employee.employeeSalaryType = newEmployee.employeeSalaryType
                    employee.employeePosition = newEmployee.employeePosition
                    employee.employeeJoinedDate = newEmployee.employeeJoinedDate
                    employee.createdAt = newEmployee.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(employee)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to validate employee", false)
            }
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Employee Item", false)
        }
    }

    override suspend fun updateEmployee(newEmployee: Employee, employeeId: String): Resource<Boolean> {
        return try {
            val validateEmployeeName = validateEmployeeName(newEmployee.employeeName)
            val validateEmployeePhone = validateEmployeePhone(newEmployee.employeePhone)
            val validateEmployeePosition = validateEmployeePosition(newEmployee.employeePosition)
            val validateEmployeeSalary = validateEmployeeSalary(newEmployee.employeeSalary)

            val hasError = listOf(validateEmployeeName, validateEmployeePhone, validateEmployeePosition, validateEmployeeSalary).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()
                    if (employee != null) {
                        realm.write {
                            findLatest(employee)?.apply {
                                this.employeeName = newEmployee.employeeName
                                this.employeePhone = newEmployee.employeePhone
                                this.employeeType = newEmployee.employeeType
                                this.employeeSalary = newEmployee.employeeSalary
                                this.employeeSalaryType = newEmployee.employeeSalaryType
                                this.employeePosition = newEmployee.employeePosition
                                this.employeeJoinedDate = newEmployee.employeeJoinedDate
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find employee", false)
                    }
                }
            }else {
                Resource.Error("Unable to validate employee", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.", false)
        }
    }

    override suspend fun deleteEmployee(employeeId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

                if (employee != null) {
                    realm.write {
                        val salary = this.query<EmployeeSalary>("employee.employeeId == $0", employeeId).find()
                        val attendance = this.query<EmployeeAttendance>("employee.employeeId == $0", employeeId).find()

                        if (salary.isNotEmpty()) {
                            delete(salary)
                        }

                        if (attendance.isNotEmpty()) {
                            delete(attendance)
                        }

                        findLatest(employee)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find employee", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete employee", false)
        }
    }

    override fun validateEmployeeName(name: String, employeeId: String?): ValidationResult {
        if(name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        if(name.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not contain any digit",
            )
        }

        if(name.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must be more than 4 characters",
            )
        }

        if(findEmployeeByName(name, employeeId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name already exists.",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateEmployeePhone(phone: String, employeeId: String?): ValidationResult {
        if(phone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not be empty",
            )
        }

        if(phone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must be 10(${phone.length}) digits",
            )
        }

        if(phone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must not contain a letter",
            )
        }

        if(findEmployeeByPhone(phone, employeeId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no already exists",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateEmployeePosition(position: String): ValidationResult {
        if (position.isEmpty()){
            return ValidationResult(false, "Employee position is required")
        }

        return ValidationResult(true)
    }

    override fun validateEmployeeSalary(salary: String): ValidationResult {
        if (salary.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not be empty",
            )
        }

        if(salary.length > 5){
            return ValidationResult(
                successful = false,
                errorMessage = "Salary is in invalid",
            )
        }

        if(salary.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not contain any characters",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}