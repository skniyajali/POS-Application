package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class DeleteEmployee(
    private val employeeRepository: EmployeeRepository,
) {

    suspend operator fun invoke(employeeId: String): Resource<Boolean> {
        return employeeRepository.deleteEmployee(employeeId)
    }
}