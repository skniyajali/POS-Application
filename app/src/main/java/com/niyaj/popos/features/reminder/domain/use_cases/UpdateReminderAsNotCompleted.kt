package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderAsNotCompleted @Inject constructor(
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(reminderId: String) {
        reminderRepository.updateReminderAsNotCompleted(reminderId)
    }
}