package com.niyaj.model

data class EmployeePayments(
    val startDate: String = "",
    val endDate: String = "",
    val payments: List<Payment> = emptyList(),
)