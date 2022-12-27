package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.util.CalculatedSalary

class GetSalaryByEmployeeId(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(employeeId: String, selectedDate: Pair<String, String>) : Resource<CalculatedSalary?> {
        return salaryRepository.getSalaryByEmployeeId(employeeId, selectedDate)
    }
}