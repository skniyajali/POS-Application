package com.niyaj.popos.features.reminder.domain.use_cases

data class ReminderUseCases(
    val getAllReminders : GetAllReminders,
    val getAbsentReminder : GetAbsentReminder,
    val getDailySalaryReminder : GetDailySalaryReminder,
    val createOrUpdateAbsentReminder : CreateOrUpdateAbsentReminder,
    val updateReminderAsNotCompleted : UpdateReminderAsNotCompleted,
    val createOrUpdateDailySalaryReminder : CreateOrUpdateDailySalaryReminder,
    val getReminderEmployees : GetReminderEmployees,
    val deleteReminder : DeleteReminder
)
