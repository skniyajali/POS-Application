package com.niyaj.data.mapper

import com.niyaj.database.model.ReminderEntity
import com.niyaj.model.DailySalaryReminder
import com.niyaj.model.Reminder

internal fun DailySalaryReminder.toEntity(): ReminderEntity {
    return ReminderEntity(
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

fun DailySalaryReminder.toReminder(): Reminder {
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
        updatedAt = this.updatedAt
    )
}