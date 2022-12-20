package com.niyaj.popos.features.employee_salary.presentation

import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary

data class SalaryState(
    val salary: List<EmployeeSalary> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: String? = null
)
