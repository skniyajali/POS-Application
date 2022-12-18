package com.niyaj.popos.domain.model

data class Employee(
    val employeeId: String = "",
    val employeeName: String = "",
    val employeePhone: String = "",
    val employeeSalary: String = "",
    val employeeSalaryType: String = "",
    val employeePosition: String = "",
    val employeeType: String = "",
    val employeeJoinedDate: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
