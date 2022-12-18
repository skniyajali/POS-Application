package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.SalaryCalculation
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetEmployeeSalary(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String): Flow<Resource<List<SalaryCalculation>>> {
        return salaryRepository.getEmployeeSalary(employeeId)
    }
}