package com.niyaj.popos.presentation.expenses_category

data class AddEditExpensesCategoryState(
    val expensesCategoryName: String = "",
    val expensesCategoryNameError: String? = null,
)
