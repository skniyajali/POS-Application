package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository

class CreateNewEmployee(
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(newEmployee: Employee): Resource<Boolean> {
        return employeeRepository.createNewEmployee(newEmployee)
    }
}