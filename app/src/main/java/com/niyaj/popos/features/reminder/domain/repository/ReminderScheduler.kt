package com.niyaj.popos.features.reminder.domain.repository

import com.niyaj.popos.features.reminder.domain.model.AbsentReminder

interface ReminderScheduler {

    suspend fun schedule(newReminder: AbsentReminder)

    fun cancel(createdReminder: AbsentReminder)
}