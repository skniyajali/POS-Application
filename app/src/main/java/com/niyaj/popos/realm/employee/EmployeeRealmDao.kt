package com.niyaj.popos.realm.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface EmployeeRealmDao {

    suspend fun getAllEmployee(): Flow<Resource<List<EmployeeRealm>>>

    suspend fun getEmployeeById(employeeId: String): Resource<EmployeeRealm?>

    fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean

    fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean

    suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun updateEmployee(newEmployee: Employee, employeeId: String): Resource<Boolean>

    suspend fun deleteEmployee(employeeId: String): Resource<Boolean>

}