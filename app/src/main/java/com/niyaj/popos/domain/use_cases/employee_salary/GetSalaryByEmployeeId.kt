package com.niyaj.popos.domain.use_cases.employee_salary

import com.niyaj.popos.domain.model.CalculatedSalary
import com.niyaj.popos.domain.repository.SalaryRepository
import com.niyaj.popos.domain.util.Resource

class GetSalaryByEmployeeId(private val salaryRepository: SalaryRepository) {

    operator fun invoke(employeeId: String, selectedDate: Pair<String, String>) : Resource<CalculatedSalary?> {
        return salaryRepository.getSalaryByEmployeeId(employeeId, selectedDate)
    }
}