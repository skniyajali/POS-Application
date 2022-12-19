package com.niyaj.popos.realm.employee.data.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.realm.expenses.ExpensesRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class EmployeeRepositoryImpl(config: RealmConfiguration) : EmployeeRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Employee Session")
    }
    override suspend fun getAllEmployee(): Flow<Resource<List<Employee>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<Employee>().sort("employeeId", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<Employee> ->
                    when (changes) {
                        is UpdatedResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                        is InitialResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                    }
                }
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to get employee items", null)
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

            val result = realm.write {
                this.copyToRealm(employee)
            }

            Resource.Success(result.isValid())
        }catch (e: RealmException){
            Resource.Error(e.message ?: "Error creating Employee Item")
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
            Resource.Error(e.message ?: "Failed to update employee.")
        }
    }

    override suspend fun deleteEmployee(employeeId: String): Resource<Boolean> {
        return try {
            realm.write {
                val employee: Employee = this.query<Employee>("employeeId == $0", employeeId).find().first()
                val expenses = this.query<ExpensesRealm>("expansesSubCategory == $0", employeeId).find()

                delete(expenses)

                delete(employee)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete employee")
        }
    }
}