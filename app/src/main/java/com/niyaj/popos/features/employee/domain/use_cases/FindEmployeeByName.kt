package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository

class FindEmployeeByName(
    private val employeeRepository: EmployeeRepository
) {
    operator fun invoke(employeeName: String, employeeId: String?) : Boolean {
        return employeeRepository.findEmployeeByName(employeeName, employeeId)
    }
}