package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository

class GetSalaryById(private val salaryRepository: SalaryRepository) {

    operator fun invoke(salaryId: String) : Resource<EmployeeSalary?> {
        return salaryRepository.getSalaryById(salaryId)
    }
}