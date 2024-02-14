package com.niyaj.model

data class EmployeeSalaryEstimation(
    val startDate: String = "",
    val endDate: String = "",
    val status: PaymentStatus = PaymentStatus.NotPaid,
    val message: String? = null,
    val remainingAmount: String = "0",
    val paymentCount: String = "",
    val absentCount: String = ""
)