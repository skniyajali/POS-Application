package com.niyaj.data.repository

import com.niyaj.model.AbsentReminder
import com.niyaj.model.DailySalaryReminder
import com.niyaj.model.EmployeeReminderWithStatus
import com.niyaj.model.Reminder
import com.niyaj.model.ReminderType
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getAllReminders(): Flow<List<Reminder>>

    suspend fun getAbsentReminder(): AbsentReminder?

    suspend fun getDailySalaryReminder(): DailySalaryReminder?

    suspend fun updateReminderAsNotCompleted(reminderId : String): Boolean

    suspend fun deleteReminder(reminderId : String): Boolean

    suspend fun getReminderEmployee(salaryDate: String, reminderType: ReminderType): Flow<List<EmployeeReminderWithStatus>>

    suspend fun createOrUpdateReminder(reminder : Reminder): Boolean
}

