package com.niyaj.popos.realm.employee.presentation.details

import com.niyaj.popos.domain.model.*
import com.niyaj.popos.realm.employee.domain.model.Employee

data class EmployeeDetailsState(
    val employee: Employee = Employee(),
    val isLoading: Boolean = false,
    val error: String? = null
)


data class EmployeePaymentState(
    val payments: CalculatedSalary = CalculatedSalary(),
    val error: String? = null,
)

data class EmployeeSalaryState(
    val payments: List<SalaryCalculation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class EmployeeSalaryDateState(
    val dates : List<SalaryCalculableDate> = emptyList(),
)

data class MonthlyAbsentReportState(
    val absents: List<AbsentReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)