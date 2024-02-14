package com.niyaj.model

data class Reminder(
    val reminderId: String = "",

    val reminderName: String = "",

    val reminderStartTime: String = "",

    val reminderEndTime: String = "",

    val reminderInterval: Int = 0,

    val reminderIntervalTimeUnit: String = "",

    val reminderType: String = "",

    val isRepeatable: Boolean = true,

    val isCompleted: Boolean = false,

    val notificationId: Int = 0,

    val updatedAt: String = "",
)