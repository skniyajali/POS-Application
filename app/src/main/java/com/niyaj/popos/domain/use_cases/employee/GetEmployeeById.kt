package com.niyaj.popos.domain.use_cases.employee

import com.niyaj.popos.domain.model.Employee
import com.niyaj.popos.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class GetEmployeeById(
    private val employeeRepository: EmployeeRepository
) {

    suspend operator fun invoke(employeeId: String): Resource<Employee?>{
        return employeeRepository.getEmployeeById(employeeId)
    }
}