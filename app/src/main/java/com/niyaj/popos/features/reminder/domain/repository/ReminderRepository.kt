package com.niyaj.popos.features.reminder.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.reminder.domain.model.AbsentReminder
import com.niyaj.popos.features.reminder.domain.model.DailySalaryReminder
import com.niyaj.popos.features.reminder.domain.model.EmployeeReminderWithStatus
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getAllReminders(): Flow<Resource<List<Reminder>>>

    suspend fun getAbsentReminder(): AbsentReminder?

    suspend fun getDailySalaryReminder(): DailySalaryReminder?

    suspend fun updateReminderAsNotCompleted(reminderId : String): Boolean

    suspend fun deleteReminder(reminderId : String): Boolean

    suspend fun getReminderEmployee(salaryDate: String, reminderType: ReminderType): Flow<Resource<List<EmployeeReminderWithStatus>>>

    suspend fun createOrUpdateReminder(reminder : Reminder): Boolean
}

