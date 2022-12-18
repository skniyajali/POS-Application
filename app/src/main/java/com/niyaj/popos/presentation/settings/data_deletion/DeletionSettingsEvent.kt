package com.niyaj.popos.presentation.settings.data_deletion

sealed class DeletionSettingsEvent {

    data class ExpensesIntervalChanged(val expensesInterval: String): DeletionSettingsEvent()

    data class ReportsIntervalChanged(val reportsInterval: String): DeletionSettingsEvent()

    data class CartIntervalChanged(val cartInterval: String): DeletionSettingsEvent()

    data class CartOrderIntervalChanged(val cartOrderInterval: String): DeletionSettingsEvent()

    object UpdateSettings: DeletionSettingsEvent()
}
