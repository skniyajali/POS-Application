package com.niyaj.popos.features.employee.data.repository

import com.niyaj.popos.features.common.util.Resource
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class EmployeeRepositoryImpl(config: RealmConfiguration) : EmployeeRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Employee Session")
    }
    override suspend fun getAllEmployee(): Flow<Resource<List<Employee>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<Employee>().sort("employeeId", Sort.DESCENDING).find().asFlow()

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
            }catch (e: Exception){
                send(Resource.Loading(false))
                send(Resource.Error(e.message ?: "Unable to get employee items", emptyList()))
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: String): Resource<Employee?> {
        return try {
            val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

            Resource.Success(employee)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Employee", null)
        }
    }

    override fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean {
        val employee = if(employeeId == null) {
            realm.query<Employee>("employeeName == $0", employeeName).first().find()
        } else {
            realm.query<Employee>("employeeId != $0 && employeeName == $1", employeeId, employeeName).first().find()
        }

        return employee != null
    }

    override fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean {
        val employee = if(employeeId == null) {
            realm.query<Employee>("employeePhone == $0", employeePhone).first().find()
        } else {
            realm.query<Employee>("employeeId != $0 && employeePhone == $1", employeeId, employeePhone).first().find()
        }

        return employee != null
    }

    override suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            val employee = Employee()
            employee.employeeId = BsonObjectId().toHexString()
            employee.employeeName = newEmployee.employeeName
            employee.employeePhone = newEmployee.employeePhone
            employee.employeeType = newEmployee.employeeType
            employee.employeeSalary = newEmployee.employeeSalary
            employee.employeeSalaryType = newEmployee.employeeSalaryType
            employee.employeePosition = newEmployee.employeePosition
            employee.employeeJoinedDate = newEmployee.employeeJoinedDate
            employee.createdAt = System.currentTimeMillis().toString()

            realm.write {
                this.copyToRealm(employee)
            }

            Resource.Success(true)
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating Employee Item", false)
        }
    }

    override suspend fun updateEmployee(
        newEmployee: Employee,
        employeeId: String,
    ): Resource<Boolean> {
        return try {
            realm.write {
                val employee = this.query<Employee>("employeeId == $0", employeeId).first().find()
                employee?.employeeName = newEmployee.employeeName
                employee?.employeePhone = newEmployee.employeePhone
                employee?.employeeType = newEmployee.employeeType
                employee?.employeeSalary = newEmployee.employeeSalary
                employee?.employeeSalaryType = newEmployee.employeeSalaryType
                employee?.employeePosition = newEmployee.employeePosition
                employee?.employeeJoinedDate = newEmployee.employeeJoinedDate
                employee?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.", false)
        }
    }

    override suspend fun deleteEmployee(employeeId: String): Resource<Boolean> {
        return try {
            realm.write {
                val employee = this.query<Employee>("employeeId == $0", employeeId).first().find()
                val salary = this.query<EmployeeSalary>("employee.employeeId == $0", employeeId).find()
                val attendance = this.query<EmployeeAttendance>("employee.employeeId == $0", employeeId).find()

                if(salary.isNotEmpty()){
                    delete(salary)
                }

                if (attendance.isNotEmpty()){
                    delete(attendance)
                }

                if (employee != null){
                    delete(employee)
                }
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete employee", false)
        }
    }
}