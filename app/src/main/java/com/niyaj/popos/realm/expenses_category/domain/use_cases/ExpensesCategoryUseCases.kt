package com.niyaj.popos.realm.expenses_category.domain.use_cases

data class ExpensesCategoryUseCases(
    val getAllExpensesCategory: GetAllExpensesCategory,
    val getExpensesCategoryById: GetExpensesCategoryById,
    val createNewExpensesCategory: CreateNewExpensesCategory,
    val updateExpensesCategory: UpdateExpensesCategory,
    val deleteExpensesCategory: DeleteExpensesCategory,
)
