package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {

    suspend fun getAllEmployee(searchText: String): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: String): Employee?

    suspend fun findEmployeeByName(employeeName: String, employeeId: String?): Boolean

    suspend fun findEmployeeByPhone(employeePhone: String, employeeId: String?): Boolean

    suspend fun doesAnyEmployeeExist(): Boolean

    suspend fun createOrUpdateEmployee(newEmployee: Employee, employeeId: String): Resource<Boolean>

    suspend fun deleteEmployees(employeeIds: List<String>): Resource<Boolean>

    suspend fun getEmployeeSalaryEstimation(employeeId: String, selectedDate: Pair<String, String>?): Flow<EmployeeSalaryEstimation>

    suspend fun getEmployeePayments(employeeId: String): Flow<List<EmployeePayments>>

    suspend fun getEmployeeAbsentDates(employeeId: String): Flow<List<EmployeeAbsentDates>>

    suspend fun getEmployeeMonthlyDate(employeeId: String): List<EmployeeMonthlyDate>

}