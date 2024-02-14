package com.niyaj.data.mapper

import com.niyaj.database.model.SettingsEntity
import com.niyaj.model.Settings

fun Settings.toEntity(): SettingsEntity {
    return SettingsEntity(
        settingsId = settingsId,
        expensesDataDeletionInterval = expensesDataDeletionInterval,
        reportDataDeletionInterval = reportDataDeletionInterval,
        cartDataDeletionInterval = cartDataDeletionInterval,
        cartOrderDataDeletionInterval = cartOrderDataDeletionInterval,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}