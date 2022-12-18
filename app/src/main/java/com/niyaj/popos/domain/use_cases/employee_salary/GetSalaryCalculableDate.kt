package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.SalaryCalculableDate
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class GetSalaryCalculableDate(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String): Resource<List<SalaryCalculableDate>> {
        return salaryRepository.getSalaryCalculableDate(employeeId)
    }
}