package com.niyaj.popos.features.employee.presentation.details

sealed class EmployeeDetailsEvent {

    data class OnChooseSalaryDate(val date: Pair<String, String>): EmployeeDetailsEvent()

    object RefreshEmployeeDetails : EmployeeDetailsEvent()
}
