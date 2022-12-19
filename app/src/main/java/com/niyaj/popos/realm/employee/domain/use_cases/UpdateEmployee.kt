package com.niyaj.popos.realm.employee.domain.use_cases

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class UpdateEmployee(
    private val employeeRepository: EmployeeRepository
) {

    suspend operator fun invoke(newEmployee: Employee, employeeId: String): Resource<Boolean>{
        return employeeRepository.updateEmployee(newEmployee, employeeId)
    }
}