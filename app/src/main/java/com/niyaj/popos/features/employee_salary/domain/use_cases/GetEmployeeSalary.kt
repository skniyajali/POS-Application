package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculation
import kotlinx.coroutines.flow.Flow

class GetEmployeeSalary(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String): Flow<Resource<List<SalaryCalculation>>> {
        return salaryRepository.getEmployeeSalary(employeeId)
    }
}