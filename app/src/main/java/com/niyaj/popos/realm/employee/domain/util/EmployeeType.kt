package com.niyaj.popos.realm.employee.domain.util


sealed class EmployeeType(val employeeType: String){
    object PartTime : EmployeeType(employeeType = "PartTime")
    object FullTime : EmployeeType(employeeType = "FullTime")
}


sealed class SalaryType(val salaryType: String){
    object Salary : SalaryType(salaryType = "Salary")
    object Advanced : SalaryType(salaryType = "Advanced")
}

sealed class PaymentType(val paymentType: String) {
    object Cash : PaymentType(paymentType = "Cash")
    object Online : PaymentType(paymentType = "Online")
    object Both : PaymentType(paymentType = "Both")
}