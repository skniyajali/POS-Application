package com.niyaj.popos.features.expenses_category.presentation

data class AddEditExpensesCategoryState(
    val expensesCategoryName: String = "",
    val expensesCategoryNameError: String? = null,
)
