package com.niyaj.popos.features.reminder.presentation.daily_salary_reminder

sealed class DailySalaryReminderEvent {

    data class SelectEmployee(val employeeId: String): DailySalaryReminderEvent()

    data class SelectDate(val date: String): DailySalaryReminderEvent()

    object SelectAllEmployee : DailySalaryReminderEvent()

    object MarkAsPaid: DailySalaryReminderEvent()
}