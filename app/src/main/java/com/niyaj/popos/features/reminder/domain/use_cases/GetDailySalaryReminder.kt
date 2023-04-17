package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class GetDailySalaryReminder @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    suspend operator fun invoke(): DailySalaryReminder? {
        return reminderRepository.getDailySalaryReminder()
    }
}