package com.niyaj.popos.realm.employee.domain.use_cases

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewEmployee(
    private val employeeRepository: EmployeeRepository
) {

    suspend operator fun invoke(newEmployee: Employee): Resource<Boolean>{
        return employeeRepository.createNewEmployee(newEmployee)
    }
}