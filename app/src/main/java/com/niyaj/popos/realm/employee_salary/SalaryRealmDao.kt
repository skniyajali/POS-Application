package com.niyaj.popos.realm.employee_salary

import com.niyaj.popos.domain.model.CalculatedSalary
import com.niyaj.popos.domain.model.EmployeeSalary
import com.niyaj.popos.domain.model.SalaryCalculableDate
import com.niyaj.popos.domain.model.SalaryCalculationRealm
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SalaryRealmDao {

    fun getAllSalary(): Flow<Resource<List<SalaryRealm>>>

    fun getSalaryById(salaryId: String): Resource<SalaryRealm?>

    fun getSalaryByEmployeeId(employeeId: String, selectedDate: Pair<String, String>): Resource<CalculatedSalary?>

    suspend fun addNewSalary(newSalary: EmployeeSalary): Resource<Boolean>

    suspend fun updateSalaryById(salaryId: String, newSalary: EmployeeSalary): Resource<Boolean>

    suspend fun deleteSalaryById(salaryId: String): Resource<Boolean>

    fun getEmployeeSalary(employeeId: String): Flow<Resource<List<SalaryCalculationRealm>>>

    fun getSalaryCalculableDate(employeeId: String): Resource<List<SalaryCalculableDate>>
}