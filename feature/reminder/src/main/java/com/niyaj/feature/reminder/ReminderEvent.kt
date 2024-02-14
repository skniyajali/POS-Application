package com.niyaj.feature.reminder

sealed class ReminderEvent {

    data class SelectReminder(val reminderId: String) : ReminderEvent()

    data class UpdateReminder(val reminderId: String) : ReminderEvent()
    
    data class DeleteReminder(val reminderId: String) : ReminderEvent()

    data object DeselectReminder : ReminderEvent()

}
