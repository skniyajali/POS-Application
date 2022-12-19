package com.niyaj.popos.realm.employee.presentation.details

sealed class EmployeeDetailsEvent {

    data class OnChooseSalaryDate(val date: Pair<String, String>): EmployeeDetailsEvent()

    object RefreshEmployeeDetails : EmployeeDetailsEvent()
}
