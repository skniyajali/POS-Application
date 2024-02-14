package com.niyaj.data.mapper

import com.niyaj.database.model.ReminderEntity
import com.niyaj.model.AbsentReminder
import com.niyaj.model.DailySalaryReminder
import com.niyaj.model.Reminder


fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        reminderId = this.reminderId,
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


fun Reminder.toAbsentReminder(): AbsentReminder {
    return AbsentReminder(
        absentRemId = this.reminderId,
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

fun Reminder.toDailySalaryReminder(): DailySalaryReminder {
    return DailySalaryReminder(
        dailySalaryRemId = this.reminderId,
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