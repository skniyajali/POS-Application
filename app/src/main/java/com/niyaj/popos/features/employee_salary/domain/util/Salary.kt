package com.niyaj.popos.features.employee_salary.domain.util

import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary

data class SalaryCalculation(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val payments: List<EmployeeSalary> = emptyList(),
)

data class SalaryCalculableDate(
    val startDate: String = "",
    val endDate: String = "",
)

data class CalculatedSalary(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val remainingAmount: String = "",
    val paymentCount: String = "",
    val absentCount: String = ""
)