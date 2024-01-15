package com.niyaj.popos.features.expenses.presentation.settings

import com.niyaj.popos.common.utils.Constants.ImportExportType
import com.niyaj.popos.features.expenses.domain.model.Expenses

sealed class ExpensesSettingsEvent {

    data class SelectExpenses(val expensesId: String) : ExpensesSettingsEvent()

    data class SelectAllExpenses(val type : ImportExportType = ImportExportType.IMPORT): ExpensesSettingsEvent()

    object DeselectExpenses : ExpensesSettingsEvent()

    object OnChooseExpenses: ExpensesSettingsEvent()

    data class ImportExpensesData(val expenses: List<Expenses> = emptyList()): ExpensesSettingsEvent()

    object ClearImportedExpenses: ExpensesSettingsEvent()

    object ImportExpenses: ExpensesSettingsEvent()

    object GetExportedExpenses: ExpensesSettingsEvent()

    object GetAllExpenses: ExpensesSettingsEvent()

    object DeletePastExpenses: ExpensesSettingsEvent()

    object DeleteAllExpenses: ExpensesSettingsEvent()
}