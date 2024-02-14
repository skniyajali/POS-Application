package com.niyaj.data.mapper

import com.niyaj.database.model.ReminderEntity
import com.niyaj.model.AbsentReminder
import com.niyaj.model.Reminder

fun AbsentReminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        reminderId = this.absentRemId,
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

fun AbsentReminder.toReminder(): Reminder {
    return Reminder(
        reminderId = this.absentRemId,
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