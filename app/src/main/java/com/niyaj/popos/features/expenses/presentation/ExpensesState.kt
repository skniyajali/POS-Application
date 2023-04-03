package com.niyaj.popos.features.expenses.presentation

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.util.FilterExpenses

data class ExpensesState(
    val expenses: List<Expenses> = emptyList(),
    val filterExpenses: FilterExpenses = FilterExpenses.ByExpensesCategory(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TotalExpensesState(
    val totalAmount: String = "0",
    val totalPayment: Int = 0,
    val selectedDate : String = "Today",
)