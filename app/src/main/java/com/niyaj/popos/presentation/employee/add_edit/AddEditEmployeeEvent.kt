package com.niyaj.popos.presentation.employee.add_edit

sealed class AddEditEmployeeEvent{

    data class EmployeeNameChanged(val employeeName: String) : AddEditEmployeeEvent()

    data class EmployeePhoneChanged(val employeePhone: String) : AddEditEmployeeEvent()

    data class EmployeeSalaryChanged(val employeeSalary: String) : AddEditEmployeeEvent()

    data class EmployeeSalaryTypeChanged(val employeeSalaryType: String) : AddEditEmployeeEvent()

    data class EmployeePositionChanged(val employeePosition: String) : AddEditEmployeeEvent()

    data class EmployeeTypeChanged(val employeeType: String) : AddEditEmployeeEvent()

    data class EmployeeJoinedDateChanged(val employeeJoinedDate: String) : AddEditEmployeeEvent()

    object CreateNewEmployee : AddEditEmployeeEvent()

    data class UpdateEmployee(val employeeId: String) : AddEditEmployeeEvent()

}
