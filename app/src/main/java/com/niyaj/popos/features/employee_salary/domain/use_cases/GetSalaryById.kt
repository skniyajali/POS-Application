package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository

class GetSalaryById(private val salaryRepository: SalaryRepository) {

    operator fun invoke(salaryId: String) : Resource<EmployeeSalary?> {
        return salaryRepository.getSalaryById(salaryId)
    }
}