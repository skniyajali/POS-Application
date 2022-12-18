package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.EmployeeSalary
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class AddNewSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(newSalary: EmployeeSalary): Resource<Boolean> {
        return salaryRepository.addNewSalary(newSalary)
    }
}