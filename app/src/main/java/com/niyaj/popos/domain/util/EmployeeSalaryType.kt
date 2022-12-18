package com.niyaj.popos.domain.util

sealed class EmployeeSalaryType(val salaryType: String){
    object Daily : EmployeeSalaryType(salaryType = "Daily")
    object Monthly : EmployeeSalaryType(salaryType = "Monthly")
    object Weekly : EmployeeSalaryType(salaryType = "Weekly")
}
