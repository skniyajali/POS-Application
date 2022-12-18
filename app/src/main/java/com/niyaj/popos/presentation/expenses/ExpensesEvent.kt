package com.niyaj.popos.presentation.expenses

import com.niyaj.popos.domain.util.filter_items.FilterExpenses

sealed class ExpensesEvent{

    data class SelectExpenses(val expensesId: String) : ExpensesEvent()

    data class DeleteExpenses(val expensesId: String) : ExpensesEvent()

    data class OnFilterExpenses(val filterExpenses: FilterExpenses): ExpensesEvent()

    data class OnSearchExpenses(val searchText: String): ExpensesEvent()

    object DeletePastExpenses: ExpensesEvent()

    object DeleteAllExpenses: ExpensesEvent()

    object ToggleSearchBar : ExpensesEvent()

    object RefreshExpenses : ExpensesEvent()
}
