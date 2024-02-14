package com.niyaj.model

import com.niyaj.common.utils.Constants

data class Settings(
    val settingsId: String = Constants.SETTINGS_ID,

    val expensesDataDeletionInterval: Int = 30,

    val reportDataDeletionInterval: Int = 30,

    val cartDataDeletionInterval: Int = 30,

    val cartOrderDataDeletionInterval: Int = 30,

    val createdAt: String = "",

    val updatedAt: String? = null,
)
