package com.niyaj.popos.presentation.employee_salary

import com.niyaj.popos.domain.model.EmployeeSalary

data class SalaryState(
    val salary: List<EmployeeSalary> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: String? = null
)
