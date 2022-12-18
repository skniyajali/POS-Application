package com.niyaj.popos.realm.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.util.Resource
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
import timber.log.Timber

class EmployeeRealmDaoImpl(config: RealmConfiguration) : EmployeeRealmDao {

    val realm = Realm.open(config)

    init {
        Timber.d("Employee Session")
    }
    override suspend fun getAllEmployee(): Flow<Resource<List<EmployeeRealm>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<EmployeeRealm>().sort("_id", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<EmployeeRealm> ->
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

    override suspend fun getEmployeeById(employeeId: String): Resource<EmployeeRealm?> {
        return try {
            val employee = realm.query<EmployeeRealm>("_id == $0", employeeId).first().find()

            Resource.Success(employee)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Employee", null)
        }
    }

    override fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean {
        val employee = if(employeeId == null) {
            realm.query<EmployeeRealm>("employeeName == $0", employeeName).first().find()
        } else {
            realm.query<EmployeeRealm>("_id != $0 && employeeName == $1", employeeId, employeeName).first().find()
        }

        return employee != null
    }

    override fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean {
        val employee = if(employeeId == null) {
            realm.query<EmployeeRealm>("employeePhone == $0", employeePhone).first().find()
        } else {
            realm.query<EmployeeRealm>("_id != $0 && employeePhone == $1", employeeId, employeePhone).first().find()
        }

        return employee != null
    }

    override suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean> {
        return try {
            val employee = EmployeeRealm()
            employee.employeeName = newEmployee.employeeName
            employee.employeePhone = newEmployee.employeePhone
            employee.employeeType = newEmployee.employeeType
            employee.employeeSalary = newEmployee.employeeSalary
            employee.employeeSalaryType = newEmployee.employeeSalaryType
            employee.employeePosition = newEmployee.employeePosition
            employee.employeeJoinedDate = newEmployee.employeeJoinedDate

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
                val employee = this.query<EmployeeRealm>("_id == $0", employeeId).first().find()
                employee?.employeeName = newEmployee.employeeName
                employee?.employeePhone = newEmployee.employeePhone
                employee?.employeeType = newEmployee.employeeType
                employee?.employeeSalary = newEmployee.employeeSalary
                employee?.employeeSalaryType = newEmployee.employeeSalaryType
                employee?.employeePosition = newEmployee.employeePosition
                employee?.employeeJoinedDate = newEmployee.employeeJoinedDate
                employee?.updated_at = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update employee.")
        }
    }

    override suspend fun deleteEmployee(employeeId: String): Resource<Boolean> {
        return try {
            realm.write {
                val employee: EmployeeRealm = this.query<EmployeeRealm>("_id == $0", employeeId).find().first()
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