package com.niyaj.feature.reminder.absent_reminder

sealed class AbsentReminderEvent {

    data class SelectEmployee(val employeeId: String): AbsentReminderEvent()

    data class SelectDate(val date: String): AbsentReminderEvent()

    data object SelectAllEmployee : AbsentReminderEvent()

    data object MarkAsAbsent: AbsentReminderEvent()
}