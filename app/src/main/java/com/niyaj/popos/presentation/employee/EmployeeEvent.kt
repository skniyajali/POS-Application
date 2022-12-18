package com.niyaj.popos.presentation.employee

import com.niyaj.popos.domain.util.filter_items.FilterEmployee

sealed class EmployeeEvent{

    object RefreshEmployee: EmployeeEvent()

    data class SelectEmployee(val employeeId: String) : EmployeeEvent()

    data class DeleteEmployee(val employeeId: String) : EmployeeEvent()

    data class OnFilterEmployee(val filterEmployee: FilterEmployee): EmployeeEvent()

    data class OnSearchEmployee(val searchText: String): EmployeeEvent()

    object ToggleSearchBar : EmployeeEvent()
}
