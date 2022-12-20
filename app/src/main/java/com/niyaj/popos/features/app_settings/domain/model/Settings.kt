package com.niyaj.popos.features.app_settings.domain.model

import com.niyaj.popos.util.Constants.SETTINGS_ID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Settings(): RealmObject {

    @PrimaryKey
    var settingsId: String = SETTINGS_ID

    var expensesDataDeletionInterval: Int = 0

    var reportDataDeletionInterval: Int = 7

    var cartDataDeletionInterval: Int = 0

    var cartOrderDataDeletionInterval: Int = 0

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        settingsId: String = "",
        expensesDataDeletionInterval: Int = 0,
        reportDataDeletionInterval: Int = 7,
        cartDataDeletionInterval: Int = 0,
        cartOrderDataDeletionInterval: Int = 0,
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