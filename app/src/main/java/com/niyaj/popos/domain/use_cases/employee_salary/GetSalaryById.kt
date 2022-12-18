package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.EmployeeSalary
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class GetSalaryById(private val salaryRepository: SalaryRepository) {

    operator fun invoke(salaryId: String) : Resource<EmployeeSalary?> {
        return salaryRepository.getSalaryById(salaryId)
    }
}