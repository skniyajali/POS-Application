package com.niyaj.popos.features.expenses.presentation

import com.niyaj.popos.features.expenses.domain.util.FilterExpenses

sealed class ExpensesEvent{

    data class SelectExpenses(val expensesId: String) : ExpensesEvent()

    data class DeleteExpenses(val expensesId: String) : ExpensesEvent()

    data class OnFilterExpenses(val filterExpenses: FilterExpenses): ExpensesEvent()

    data class OnSearchExpenses(val searchText: String): ExpensesEvent()

    data class OnSelectDate(val selectedDate: String): ExpensesEvent()

    object DeletePastExpenses: ExpensesEvent()

    object DeleteAllExpenses: ExpensesEvent()

    object ToggleSearchBar : ExpensesEvent()

    object RefreshExpenses : ExpensesEvent()
}
