package com.niyaj.popos.realm.employee_salary.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.realm.employee_salary.domain.util.CalculatedSalary

class GetSalaryByEmployeeId(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String, selectedDate: Pair<String, String>) : Resource<CalculatedSalary?> {
        return salaryRepository.getSalaryByEmployeeId(employeeId, selectedDate)
    }
}