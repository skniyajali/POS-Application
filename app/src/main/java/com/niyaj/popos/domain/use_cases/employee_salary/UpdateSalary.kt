package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.EmployeeSalary
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class UpdateSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(salaryId: String, newSalary: EmployeeSalary) : Resource<Boolean> {
        return salaryRepository.updateSalaryById(salaryId, newSalary)
    }
}