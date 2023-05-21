package com.niyaj.popos.features.employee.presentation.details

import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentReport
import com.niyaj.popos.features.employee_salary.domain.util.CalculatedSalary
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculableDate
import com.niyaj.popos.features.employee_salary.domain.util.SalaryCalculation

data class EmployeeDetailsState(
    val employee: Employee? = null,
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