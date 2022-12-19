package com.niyaj.popos.realm.employee.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository

class DeleteEmployee(
    private val employeeRepository: EmployeeRepository,
) {

    suspend operator fun invoke(employeeId: String): Resource<Boolean> {
        return employeeRepository.deleteEmployee(employeeId)
    }
}