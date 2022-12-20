package com.niyaj.popos.features.expenses.presentation.add_edit

sealed class AddEditExpensesEvent {
    data class ExpensesCategoryNameChanged(val expensesCategoryName: String, val expensesCategoryId: String) : AddEditExpensesEvent()

    data class ExpensesPriceChanged(val expensesPrice: String) : AddEditExpensesEvent()

    data class ExpensesRemarksChanged(val expensesRemarks: String) : AddEditExpensesEvent()

    data class OnSearchExpensesCategory(val searchText: String): AddEditExpensesEvent()

    object CreateNewExpenses : AddEditExpensesEvent()

    data class UpdateExpenses(val expensesId: String) : AddEditExpensesEvent()
}
