package com.niyaj.feature.employee.details

sealed class EmployeeDetailsEvent {
    data class OnChooseSalaryDate(val date: Pair<String, String>) : EmployeeDetailsEvent()

    data object RefreshData: EmployeeDetailsEvent()
}
