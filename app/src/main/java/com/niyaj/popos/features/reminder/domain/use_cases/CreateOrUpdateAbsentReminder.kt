package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class CreateOrUpdateAbsentReminder @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    suspend operator fun invoke(absentReminder: AbsentReminder): Boolean {
        return reminderRepository.createOrUpdateReminder(absentReminder.toReminder())
    }
}
