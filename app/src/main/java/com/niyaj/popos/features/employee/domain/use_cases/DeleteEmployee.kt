package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository

class DeleteEmployee(
    private val employeeRepository: EmployeeRepository,
) {
    suspend operator fun invoke(employeeId: String): Resource<Boolean> {
        return employeeRepository.deleteEmployee(employeeId)
    }
}