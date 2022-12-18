package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.repository.EmployeeRepository

class FindEmployeeByPhone(
    private val employeeRepository: EmployeeRepository
) {

    operator fun invoke(employeePhone: String, employeeId: String?) : Boolean {
        return employeeRepository.findEmployeeByPhone(employeePhone, employeeId)
    }
}