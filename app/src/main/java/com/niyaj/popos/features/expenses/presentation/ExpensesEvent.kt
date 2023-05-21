package com.niyaj.popos.features.expenses.presentation

sealed class ExpensesEvent{

    data class SelectExpenses(val expensesId: String) : ExpensesEvent()

    data class DeleteExpenses(val expensesId: String) : ExpensesEvent()

    data class OnSearchExpenses(val searchText: String): ExpensesEvent()

    data class OnSelectDate(val selectedDate: String): ExpensesEvent()

    object ToggleSearchBar : ExpensesEvent()

    object RefreshExpenses : ExpensesEvent()
}
