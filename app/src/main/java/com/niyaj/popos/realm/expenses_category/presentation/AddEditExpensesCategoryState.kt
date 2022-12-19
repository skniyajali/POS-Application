package com.niyaj.popos.realm.expenses_category.presentation

data class AddEditExpensesCategoryState(
    val expensesCategoryName: String = "",
    val expensesCategoryNameError: String? = null,
)
