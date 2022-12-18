package com.niyaj.popos.presentation.employee.details

sealed class EmployeeDetailsEvent {

    data class OnChooseSalaryDate(val date: Pair<String, String>): EmployeeDetailsEvent()

    object RefreshEmployeeDetails : EmployeeDetailsEvent()
}
