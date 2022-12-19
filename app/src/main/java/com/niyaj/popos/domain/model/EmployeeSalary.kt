package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee_salary.SalaryRealm

data class EmployeeSalary(
    val salaryId: String = "",
    val employee: Employee,
    val salaryType: String,
    val employeeSalary: String,
    val salaryGivenDate: String,
    val salaryPaymentType: String,
    val salaryNote: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)


data class SalaryCalculationRealm(
    val startDate: String = "",
    val endDate: String = "",
    val status: String = "",
    val message: String? = null,
    val payments: List<SalaryRealm> = emptyList(),
)

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