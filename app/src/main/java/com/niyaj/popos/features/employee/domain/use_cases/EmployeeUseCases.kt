package com.niyaj.popos.features.employee.domain.use_cases

import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeName
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePhone
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePosition
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeSalary

data class EmployeeUseCases(
    val validateEmployeeName: ValidateEmployeeName,
    val validateEmployeePhone: ValidateEmployeePhone,
    val validateEmployeePosition: ValidateEmployeePosition,
    val validateEmployeeSalary: ValidateEmployeeSalary,
    val getAllEmployee: GetAllEmployee,
    val getEmployeeById: GetEmployeeById,
    val createNewEmployee: CreateNewEmployee,
    val updateEmployee: UpdateEmployee,
    val deleteEmployee: DeleteEmployee,
)
