package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class UpdateEmployee(
    private val employeeRepository: EmployeeRepository
) {

    suspend operator fun invoke(newEmployee: Employee, employeeId: String): Resource<Boolean>{
        return employeeRepository.updateEmployee(newEmployee, employeeId)
    }
}