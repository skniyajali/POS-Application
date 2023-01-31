package com.niyaj.popos.features.reminder.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reminder.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllReminders @Inject constructor(
    private val reminderRepository : ReminderRepository
) {
    operator fun invoke(): Flow<Resource<List<Reminder>>> {
        return reminderRepository.getAllReminders()
    }
}