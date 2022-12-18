package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class DeleteSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(salaryId: String): Resource<Boolean> {
        return salaryRepository.deleteSalaryById(salaryId)
    }
}