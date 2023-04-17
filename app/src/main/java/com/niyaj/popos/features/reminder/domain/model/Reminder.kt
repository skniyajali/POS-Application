package com.niyaj.popos.features.reminder.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Reminder() : RealmObject {
    @PrimaryKey
    var reminderId: String = ""

    var reminderName: String = ""

    var reminderStartTime: String = ""

    var reminderEndTime: String = ""

    var reminderInterval: Int = 0

    var reminderIntervalTimeUnit: String = ""

    var reminderType: String = ""

    var isRepeatable: Boolean = true

    var isCompleted: Boolean = false

    var updatedAt: String = ""

    constructor(
        reminderId: String = "",
        reminderName: String = "",
        reminderStartTime: String = "",
        reminderEndTime: String = "",
        reminderInterval: Int = 0,
        reminderIntervalTimeUnit: String = "",
        reminderType: String = "",
        isRepeatable: Boolean = true,
        isCompleted: Boolean = false,
        updatedAt: String = ""
    ) : this() {
        this.reminderId = reminderId
        this.reminderName = reminderName
        this.reminderStartTime = reminderStartTime
        this.reminderEndTime = reminderEndTime
        this.reminderInterval = reminderInterval
        this.reminderIntervalTimeUnit = reminderIntervalTimeUnit
        this.reminderType = reminderType
        this.isRepeatable = isRepeatable
        this.isCompleted = isCompleted
        this.updatedAt = updatedAt
    }
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
        updatedAt = this.updatedAt
    )
}

internal fun Reminder.toDailySalaryReminder(): DailySalaryReminder {
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
        updatedAt = this.updatedAt
    )
}
