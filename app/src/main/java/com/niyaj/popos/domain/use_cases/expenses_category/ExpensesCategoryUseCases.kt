package com.niyaj.popos.domain.use_cases.expenses_category

data class ExpensesCategoryUseCases(
    val getAllExpensesCategory: GetAllExpensesCategory,
    val getExpensesCategoryById: GetExpensesCategoryById,
    val createNewExpensesCategory: CreateNewExpensesCategory,
    val updateExpensesCategory: UpdateExpensesCategory,
    val deleteExpensesCategory: DeleteExpensesCategory,
)
