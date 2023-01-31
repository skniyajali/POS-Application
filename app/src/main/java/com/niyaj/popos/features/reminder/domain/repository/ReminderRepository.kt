package com.niyaj.popos.features.reminder.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getAllReminders(): Flow<Resource<List<Reminder>>>

    fun getAttendanceReminder(): AbsentReminder

    suspend fun createOrUpdateAttendanceReminder(absentReminder: AbsentReminder): Boolean

    suspend fun createOrUpdateDailySalaryReminder(newReminder : Reminder, reminderId: String? = null): Boolean
}
