package com.niyaj.popos.realm.employee.domain.use_cases

import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository

class FindEmployeeByPhone(
    private val employeeRepository: EmployeeRepository
) {

    operator fun invoke(employeePhone: String, employeeId: String?) : Boolean {
        return employeeRepository.findEmployeeByPhone(employeePhone, employeeId)
    }
}