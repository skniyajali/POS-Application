package com.niyaj.popos.realm.employee_salary.presentation

sealed class SalaryEvent {

    object RefreshSalary: SalaryEvent()

    data class SelectSalary(val salaryId: String) : SalaryEvent()

    data class SelectEmployee(val employeeId: String) : SalaryEvent()

    data class DeleteSalary(val salaryId: String) : SalaryEvent()

    data class OnSearchSalary(val searchText: String): SalaryEvent()

    object ToggleSearchBar : SalaryEvent()
}
