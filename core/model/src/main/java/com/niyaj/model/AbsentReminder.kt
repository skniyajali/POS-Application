package com.niyaj.model

import com.niyaj.common.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.common.utils.Constants.ABSENT_REMINDER_INTERVAL
import com.niyaj.common.utils.Constants.ABSENT_REMINDER_NAME
import com.niyaj.common.utils.Constants.ABSENT_REMINDER_TIME_UNIT
import com.niyaj.common.utils.closingTime
import com.niyaj.common.utils.openingTime
import kotlin.random.Random

data class AbsentReminder(

    val absentRemId: String = ABSENT_REMINDER_ID,

    val reminderName: String = ABSENT_REMINDER_NAME,

    val reminderStartTime: String = openingTime,

    val reminderEndTime: String = closingTime,

    val reminderInterval: Int = ABSENT_REMINDER_INTERVAL,

    val reminderIntervalTimeUnit: String = ABSENT_REMINDER_TIME_UNIT.name,

    val reminderType: String = ReminderType.Attendance.reminderType,

    val isRepeatable: Boolean = true,

    val isCompleted: Boolean = false,

    val notificationId: Int = Random.nextInt(),

    val updatedAt: String = "",
)