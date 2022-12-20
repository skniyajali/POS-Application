package com.niyaj.popos.features.employee.domain.use_cases

data class EmployeeUseCases(
    val getAllEmployee: GetAllEmployee,
    val getEmployeeById: GetEmployeeById,
    val findEmployeeByName: FindEmployeeByName,
    val findEmployeeByPhone: FindEmployeeByPhone,
    val createNewEmployee: CreateNewEmployee,
    val updateEmployee: UpdateEmployee,
    val deleteEmployee: DeleteEmployee,
)
