package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository

class DeleteSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(salaryId: String): Resource<Boolean> {
        return salaryRepository.deleteSalaryById(salaryId)
    }
}