package com.niyaj.feature.reminder.daily_salary_reminder

sealed class DailySalaryReminderEvent {

    data class SelectEmployee(val employeeId: String): DailySalaryReminderEvent()

    data class SelectDate(val date: String): DailySalaryReminderEvent()

    data object SelectAllEmployee : DailySalaryReminderEvent()

    data object MarkAsPaid: DailySalaryReminderEvent()
}