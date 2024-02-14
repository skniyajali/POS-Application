package com.niyaj.feature.expenses.settings

import com.niyaj.common.utils.Constants.ImportExportType
import com.niyaj.model.Expenses

sealed class ExpensesSettingsEvent {

    data class SelectExpenses(val expensesId: String) : ExpensesSettingsEvent()

    data class SelectAllExpenses(val type : ImportExportType = ImportExportType.IMPORT): ExpensesSettingsEvent()

    data object DeselectExpenses : ExpensesSettingsEvent()

    data object OnChooseExpenses: ExpensesSettingsEvent()

    data class ImportExpensesData(val expenses: List<Expenses> = emptyList()): ExpensesSettingsEvent()

    data object ClearImportedExpenses: ExpensesSettingsEvent()

    data object ImportExpenses: ExpensesSettingsEvent()

    data object GetExportedExpenses: ExpensesSettingsEvent()

    data object GetAllExpenses: ExpensesSettingsEvent()

    data object DeletePastExpenses: ExpensesSettingsEvent()

    data object DeleteAllExpenses: ExpensesSettingsEvent()
}