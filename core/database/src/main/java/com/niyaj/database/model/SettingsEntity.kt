package com.niyaj.database.model

import com.niyaj.common.utils.Constants.SETTINGS_ID
import com.niyaj.model.Settings
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SettingsEntity(): RealmObject {

    @PrimaryKey
    var settingsId: String = SETTINGS_ID

    var expensesDataDeletionInterval: Int = 30

    var reportDataDeletionInterval: Int = 30

    var cartDataDeletionInterval: Int = 30

    var cartOrderDataDeletionInterval: Int = 30

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        settingsId: String = "",
        expensesDataDeletionInterval: Int = 30,
        reportDataDeletionInterval: Int = 30,
        cartDataDeletionInterval: Int = 30,
        cartOrderDataDeletionInterval: Int = 30,
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ): this() {
        this.settingsId = settingsId
        this.expensesDataDeletionInterval = expensesDataDeletionInterval
        this.reportDataDeletionInterval = reportDataDeletionInterval
        this.cartDataDeletionInterval = cartDataDeletionInterval
        this.cartOrderDataDeletionInterval = cartOrderDataDeletionInterval
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}


fun SettingsEntity.toExternalModel(): Settings {
    return Settings(
        settingsId = settingsId,
        expensesDataDeletionInterval = expensesDataDeletionInterval,
        reportDataDeletionInterval = reportDataDeletionInterval,
        cartDataDeletionInterval = cartDataDeletionInterval,
        cartOrderDataDeletionInterval = cartOrderDataDeletionInterval,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}