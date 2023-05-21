package com.niyaj.popos.features.employee.presentation

import com.niyaj.popos.features.employee.domain.model.Employee

/**
 * EmployeeState is a data class that holds the state of the EmployeeViewModel.
 * It is used to update the UI.
 * @param employees: List of employees
 * @param isLoading: Boolean to show loading indicator
 * @param error: String to show error message
 */
data class EmployeeState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
