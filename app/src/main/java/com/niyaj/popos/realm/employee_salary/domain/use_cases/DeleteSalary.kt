package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository

class DeleteSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(salaryId: String): Resource<Boolean> {
        return salaryRepository.deleteSalaryById(salaryId)
    }
}