package com.niyaj.popos.features.reminder.domain.model

import com.niyaj.popos.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.common.utils.Constants.DAILY_SALARY_REMINDER_INTERVAL
import com.niyaj.popos.common.utils.Constants.DAILY_SALARY_REMINDER_NAME
import com.niyaj.popos.common.utils.Constants.DAILY_SALARY_REMINDER_TIME_UNIT
import com.niyaj.popos.common.utils.closingTime
import com.niyaj.popos.common.utils.dailySalaryStartTime
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import kotlin.random.Random

data class DailySalaryReminder(
    val dailySalaryRemId: String = DAILY_SALARY_REMINDER_ID,

    val reminderName: String = DAILY_SALARY_REMINDER_NAME,

    val reminderStartTime: String = dailySalaryStartTime,

    val reminderEndTime: String = closingTime,

    val reminderInterval: Int = DAILY_SALARY_REMINDER_INTERVAL,

    val reminderIntervalTimeUnit: String = DAILY_SALARY_REMINDER_TIME_UNIT.name,

    val reminderType: String = ReminderType.DailySalary.reminderType,

    val isRepeatable: Boolean = true,

    val isCompleted: Boolean = false,

    val notificationId: Int = Random.nextInt(),

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
        notificationId = this.notificationId,
        updatedAt = System.currentTimeMillis().toString()
    )
}