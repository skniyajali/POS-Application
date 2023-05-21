package com.niyaj.popos.features.employee.presentation

sealed class EmployeeEvent{

    object RefreshEmployee: EmployeeEvent()

    data class SelectEmployee(val employeeId: String) : EmployeeEvent()

    data class DeleteEmployee(val employeeId: String) : EmployeeEvent()
    
    data class OnSearchEmployee(val searchText: String): EmployeeEvent()

    object ToggleSearchBar : EmployeeEvent()
}
