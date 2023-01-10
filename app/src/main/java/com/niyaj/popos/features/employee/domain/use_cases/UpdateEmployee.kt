package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository

class UpdateEmployee(
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(newEmployee: Employee, employeeId: String): Resource<Boolean> {
        return employeeRepository.updateEmployee(newEmployee, employeeId)
    }
}