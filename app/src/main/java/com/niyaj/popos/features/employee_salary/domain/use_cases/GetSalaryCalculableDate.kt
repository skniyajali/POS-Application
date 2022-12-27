package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculableDate

class GetSalaryCalculableDate(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(employeeId: String): Resource<List<SalaryCalculableDate>> {
        return salaryRepository.getSalaryCalculableDate(employeeId)
    }
}