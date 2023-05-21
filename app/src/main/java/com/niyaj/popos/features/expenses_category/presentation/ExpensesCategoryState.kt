package com.niyaj.popos.features.expenses_category.presentation

import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory

data class ExpensesCategoryState(
    val expensesCategory: List<ExpensesCategory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
