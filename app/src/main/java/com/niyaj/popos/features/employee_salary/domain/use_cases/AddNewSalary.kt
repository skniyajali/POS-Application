package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository

class AddNewSalary(private val salaryRepository: SalaryRepository) {
    suspend operator fun invoke(newSalary: EmployeeSalary): Resource<Boolean> {
        return salaryRepository.addNewSalary(newSalary)
    }
}