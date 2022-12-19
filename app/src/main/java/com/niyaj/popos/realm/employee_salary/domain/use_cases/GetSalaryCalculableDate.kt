package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.realm.employee_salary.domain.util.SalaryCalculableDate

class GetSalaryCalculableDate(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String): Resource<List<SalaryCalculableDate>> {
        return salaryRepository.getSalaryCalculableDate(employeeId)
    }
}