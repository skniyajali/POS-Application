package com.niyaj.popos.features.reminder.domain.model

import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.util.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.util.Constants.DAILY_SALARY_REMINDER_NAME
import com.niyaj.popos.util.closingTime
import com.niyaj.popos.util.dailySalaryStartTime
import java.util.concurrent.TimeUnit

data class DailySalaryReminder(
    val dailySalaryRemId: String = DAILY_SALARY_REMINDER_ID,

    val reminderName: String = DAILY_SALARY_REMINDER_NAME,

    val reminderStartTime: String = dailySalaryStartTime,

    val reminderEndTime: String = closingTime,

    val reminderInterval: Int = 20,

    val reminderIntervalTimeUnit: String = TimeUnit.MINUTES.name,

    val reminderType: String = ReminderType.DailySalary.reminderType,

    val isRepeatable: Boolean = true,

    val isCompleted: Boolean = false,

    val updatedAt: String = "",
)

internal fun DailySalaryReminder.toReminder(): Reminder {
    return Reminder(
        reminderId = this.dailySalaryRemId,
        reminderName = this.reminderName,
        reminderStartTime = this.reminderStartTime,
        reminderEndTime = this.reminderEndTime,
        reminderInterval = this.reminderInterval,
        reminderIntervalTimeUnit = this.reminderIntervalTimeUnit,
        reminderType = this.reminderType,
        isRepeatable = this.isRepeatable,
        isCompleted = this.isCompleted,
        updatedAt = System.currentTimeMillis().toString()
    )
}