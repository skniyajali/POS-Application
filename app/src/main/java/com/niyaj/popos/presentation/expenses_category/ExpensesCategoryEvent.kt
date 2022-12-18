package com.niyaj.popos.presentation.expenses_category

import com.niyaj.popos.domain.util.filter_items.FilterExpensesCategory

sealed class ExpensesCategoryEvent {

    data class ExpensesCategoryNameChanged(val expensesCategoryName: String) : ExpensesCategoryEvent()

    data class SelectExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    object CreateNewExpensesCategory : ExpensesCategoryEvent()

    data class UpdateExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    data class DeleteExpensesCategory(val expensesCategoryId: String) : ExpensesCategoryEvent()

    data class OnFilterExpensesCategory(val filterExpensesCategory: FilterExpensesCategory): ExpensesCategoryEvent()

    data class OnSearchExpensesCategory(val searchText: String): ExpensesCategoryEvent()

    object ToggleSearchBar : ExpensesCategoryEvent()

    object RefreshExpenses: ExpensesCategoryEvent()
}
