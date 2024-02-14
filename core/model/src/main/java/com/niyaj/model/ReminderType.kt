package com.niyaj.model

sealed class ReminderType(val reminderType : String) {
    object Attendance : ReminderType("Attendance")
    object DailySalary : ReminderType("Daily Salary")
    object MonthlySalary : ReminderType("Monthly Salary")
}