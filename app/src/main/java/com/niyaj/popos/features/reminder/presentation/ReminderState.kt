package com.niyaj.popos.features.reminder.presentation

import com.niyaj.popos.features.reminder.domain.model.Reminder

data class ReminderState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val hasErrors: String? = null,
)
