package com.niyaj.popos.features.reminder.presentation.absent_reminder

sealed class AbsentReminderEvent {
    data class SelectEmployee(val employeeId: String): AbsentReminderEvent()

    object SelectAllEmployee : AbsentReminderEvent()

    object MarkAbsent: AbsentReminderEvent()
}
