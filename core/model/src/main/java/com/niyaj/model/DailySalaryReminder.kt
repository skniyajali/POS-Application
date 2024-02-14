package com.niyaj.model

import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_INTERVAL
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_NAME
import com.niyaj.common.utils.Constants.DAILY_SALARY_REMINDER_TIME_UNIT
import com.niyaj.common.utils.closingTime
import com.niyaj.common.utils.dailySalaryStartTime
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