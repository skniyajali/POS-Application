package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository

class AddNewSalary(private val salaryRepository: SalaryRepository) {

    suspend operator fun invoke(newSalary: EmployeeSalary): Resource<Boolean> {
        return salaryRepository.addNewSalary(newSalary)
    }
}