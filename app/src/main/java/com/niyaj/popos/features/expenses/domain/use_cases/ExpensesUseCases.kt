package com.niyaj.popos.features.expenses.domain.use_cases

data class ExpensesUseCases(
    val getAllExpenses: GetAllExpenses,
    val getExpensesById: GetExpensesById,
    val createNewExpenses: CreateNewExpenses,
    val updateExpenses: UpdateExpenses,
    val deleteExpenses: DeleteExpenses,
    val deletePastExpenses: DeletePastExpenses,
)
