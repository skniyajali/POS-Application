package com.niyaj.popos.features.reminder.domain.use_cases

data class ReminderUseCases(
    val getAllReminders : GetAllReminders,
    val getAbsentReminder : GetAbsentReminder,
    val createOrUpdateAbsentReminder : CreateOrUpdateAbsentReminder
)
