package com.niyaj.popos.realm.expenses.presentation

import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.expenses.domain.util.FilterExpenses
import com.niyaj.popos.realm.expenses.domain.model.Expenses

data class ExpensesState(
    val expenses: List<Expenses> = emptyList(),
    val filterExpenses: FilterExpenses = FilterExpenses.ByExpensesCategory(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null,
)
