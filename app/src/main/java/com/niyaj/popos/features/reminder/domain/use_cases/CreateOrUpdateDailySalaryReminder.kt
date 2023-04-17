package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.model.toReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class CreateOrUpdateDailySalaryReminder @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    suspend operator fun invoke(dailySalaryReminder : DailySalaryReminder): Boolean {
        return reminderRepository.createOrUpdateReminder(dailySalaryReminder.toReminder())
    }
}