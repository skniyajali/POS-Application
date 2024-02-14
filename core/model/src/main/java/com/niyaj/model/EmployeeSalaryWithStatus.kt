package com.niyaj.model

data class EmployeeReminderWithStatus(
    val employee : Employee = Employee(),
    val paymentStatus : PaymentStatus = PaymentStatus.NotPaid,
    val absentStatus: Boolean = false,
    val reminderType: ReminderType = ReminderType.Attendance,
    val employeeSalary: String = "0",
)