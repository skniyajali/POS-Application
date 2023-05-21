package com.niyaj.popos.features.expenses_category.presentation

sealed class ExpensesCategoryEvent {

    data class ExpensesCategoryNameChanged(val expensesCategoryName: String) :
        ExpensesCategoryEvent()

    data class SelectExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    object CreateNewExpensesCategory : ExpensesCategoryEvent()

    data class UpdateExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    data class DeleteExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    data class OnSearchExpensesCategory(val searchText: String) : ExpensesCategoryEvent()

    object ToggleSearchBar : ExpensesCategoryEvent()

    object RefreshExpenses : ExpensesCategoryEvent()
}
