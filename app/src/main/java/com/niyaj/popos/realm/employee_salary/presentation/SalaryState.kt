package com.niyaj.popos.realm.employee_salary.presentation

import com.niyaj.popos.realm.employee_salary.domain.model.EmployeeSalary

data class SalaryState(
    val salary: List<EmployeeSalary> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: String? = null
)
