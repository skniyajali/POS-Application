package com.niyaj.popos.features.reminder.presentation

sealed class ReminderEvent {

    data class SelectReminder(val reminderId: String) : ReminderEvent()

    data class UpdateReminder(val reminderId: String) : ReminderEvent()
    
    data class DeleteReminder(val reminderId: String) : ReminderEvent()

    object DeselectReminder : ReminderEvent()

    object RefreshReminder : ReminderEvent()
}
