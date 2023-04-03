package com.niyaj.popos.features.expenses.presentation.add_edit

import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.util.toMilliSecond
import java.time.LocalDate

data class AddEditExpensesState(

    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expensesCategoryError: String? = null,

    val expensesPrice: String = "",
    val expensesPriceError: String? = null,

    val expensesRemarks: String = "",

    val expensesGivenDate: String = LocalDate.now().toMilliSecond
)
