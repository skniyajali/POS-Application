package com.niyaj.app_settings.data_deletion

data class DeletionSettingsState(
    val expensesInterval: String = "",
    val reportsInterval: String = "",
    val cartInterval: String = "",
    val cartOrderInterval: String = "",
)
