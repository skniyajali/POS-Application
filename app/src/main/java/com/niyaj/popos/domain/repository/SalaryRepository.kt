package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.*
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SalaryRepository {

    fun getAllSalary(): Flow<Resource<List<EmployeeSalary>>>

    fun getSalaryById(salaryId: String): Resource<EmployeeSalary?>

    fun getSalaryByEmployeeId(employeeId: String, selectedDate: Pair<String, String>): Resource<CalculatedSalary?>

    suspend fun addNewSalary(newSalary: EmployeeSalary): Resource<Boolean>

    suspend fun updateSalaryById(salaryId: String, newSalary: EmployeeSalary): Resource<Boolean>

    suspend fun deleteSalaryById(salaryId: String): Resource<Boolean>

    fun getEmployeeSalary(employeeId: String): Flow<Resource<List<SalaryCalculation>>>

    fun getSalaryCalculableDate(employeeId: String): Resource<List<SalaryCalculableDate>>

}