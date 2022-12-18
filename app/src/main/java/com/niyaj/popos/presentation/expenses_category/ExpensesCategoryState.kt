package com.niyaj.popos.presentation.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpensesCategory

data class ExpensesCategoryState(
    val expensesCategory: List<ExpensesCategory> = emptyList(),
    val filterExpensesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(
        SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
