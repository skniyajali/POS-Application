package com.niyaj.popos.features.employee.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun getAllEmployee(): Flow<Resource<List<Employee>>>

    fun getEmployeeById(employeeId: String): Resource<Employee?>

    fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean

    fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean

    fun doesAnyEmployeeExist(): Boolean

    suspend fun createNewEmployee(newEmployee: Employee): Resource<Boolean>

    suspend fun updateEmployee(newEmployee: Employee, employeeId: String): Resource<Boolean>

    suspend fun deleteEmployee(employeeId: String): Resource<Boolean>

}