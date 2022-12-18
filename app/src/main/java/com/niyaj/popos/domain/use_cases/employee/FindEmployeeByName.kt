package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.repository.EmployeeRepository

class FindEmployeeByName(
    private val employeeRepository: EmployeeRepository
) {

    operator fun invoke(employeeName: String, employeeId: String?) : Boolean {
        return employeeRepository.findEmployeeByName(employeeName, employeeId)
    }
}