package com.niyaj.popos.features.employee.domain.util

sealed class EmployeeSalaryType(val salaryType: String){
    object Daily : EmployeeSalaryType(salaryType = "Daily")
    object Monthly : EmployeeSalaryType(salaryType = "Monthly")
    object Weekly : EmployeeSalaryType(salaryType = "Weekly")
}
