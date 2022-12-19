package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository

class UpdateSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(salaryId: String, newSalary: EmployeeSalary) : Resource<Boolean> {
        return salaryRepository.updateSalaryById(salaryId, newSalary)
    }
}