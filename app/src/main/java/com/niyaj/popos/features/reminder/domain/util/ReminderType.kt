package com.niyaj.popos.features.reminder.domain.util

sealed class ReminderType(val reminderType : String) {
    object Attendance : ReminderType("Attendance")
    object DailySalary : ReminderType("Daily Salary")
    object MonthlySalary : ReminderType("Monthly Salary")
}