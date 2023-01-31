package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAbsentReminder @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    operator fun invoke(): AbsentReminder {
        return reminderRepository.getAttendanceReminder()
    }
}