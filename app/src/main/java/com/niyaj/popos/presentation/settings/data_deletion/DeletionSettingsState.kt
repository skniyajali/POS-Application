package com.niyaj.popos.presentation.settings.data_deletion

data class DeletionSettingsState(
    val expensesInterval: String = "",
    val expensesIntervalError: String? = null,

    val reportsInterval: String = "",
    val reportsIntervalError: String? = null,

    val cartInterval: String = "",
    val cartIntervalError: String? = null,

    val cartOrderInterval: String = "",
    val cartOrderIntervalError: String? = null,
)
