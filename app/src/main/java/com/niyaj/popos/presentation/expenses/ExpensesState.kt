package com.niyaj.popos.presentation.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpenses

data class ExpensesState(
    val expenses: List<Expenses> = emptyList(),
    val filterExpenses: FilterExpenses = FilterExpenses.ByExpensesCategory(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null,
)
