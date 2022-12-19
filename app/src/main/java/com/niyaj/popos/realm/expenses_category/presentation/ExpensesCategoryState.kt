package com.niyaj.popos.realm.expenses_category.presentation

import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.util.FilterExpensesCategory

data class ExpensesCategoryState(
    val expensesCategory: List<ExpensesCategory> = emptyList(),
    val filterExpensesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(
        SortType.Descending
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)
