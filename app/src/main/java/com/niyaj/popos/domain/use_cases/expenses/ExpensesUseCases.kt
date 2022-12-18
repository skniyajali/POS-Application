package com.niyaj.popos.domain.use_cases.expenses

data class ExpensesUseCases(
    val getAllExpenses: GetAllExpenses,
    val getExpensesById: GetExpensesById,
    val createNewExpenses: CreateNewExpenses,
    val updateExpenses: UpdateExpenses,
    val deleteExpenses: DeleteExpenses,
    val deletePastExpenses: DeletePastExpenses,
)
