package com.niyaj.popos.presentation.expenses.add_edit

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.util.ExpensesType

data class AddEditExpensesState(

    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expensesCategoryError: String? = null,

    val expensesPrice: String = "",
    val expensesPriceError: String? = null,

    val expensesRemarks: String = "",
)
