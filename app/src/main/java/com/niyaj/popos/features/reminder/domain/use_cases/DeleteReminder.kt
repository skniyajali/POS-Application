package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminder @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    suspend operator fun invoke(reminderId: String): Boolean {
        return reminderRepository.deleteReminder(reminderId)
    }
}