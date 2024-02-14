package com.niyaj.feature.expenses.add_edit

import com.niyaj.common.utils.toMilliSecond
import com.niyaj.model.ExpensesCategory
import java.time.LocalDate

data class AddEditExpenseState(
    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expenseDate: String = LocalDate.now().toMilliSecond,
    val expenseAmount: String = "",
    val expenseNote: String = "",
)