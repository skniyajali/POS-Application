package com.niyaj.feature.expenses.add_edit

import com.niyaj.model.ExpensesCategory

sealed interface AddEditExpenseEvent {

    data class ExpensesNameChanged(val expenseName: ExpensesCategory): AddEditExpenseEvent

    data class ExpensesAmountChanged(val expenseAmount: String): AddEditExpenseEvent

    data class ExpensesDateChanged(val expenseDate: String): AddEditExpenseEvent

    data class ExpensesNoteChanged(val expenseNote: String): AddEditExpenseEvent

    data class AddOrUpdateExpense(val expenseId: String = ""): AddEditExpenseEvent
}