package com.niyaj.popos.features.expenses.presentation

import com.niyaj.popos.features.expenses.domain.model.Expenses

data class ExpensesState(
    val expenses: List<Expenses> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TotalExpensesState(
    val totalAmount: String = "0",
    val totalPayment: Int = 0,
    val selectedDate : String = "",
)