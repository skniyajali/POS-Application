package com.niyaj.popos.features.expenses_category.domain.use_cases

import com.niyaj.popos.features.expenses_category.domain.use_cases.validation.ValidateExpensesCategoryName

data class ExpensesCategoryUseCases(
    val validateExpensesCategoryName: ValidateExpensesCategoryName,
    val getAllExpensesCategory: GetAllExpensesCategory,
    val getExpensesCategoryById: GetExpensesCategoryById,
    val createNewExpensesCategory: CreateNewExpensesCategory,
    val updateExpensesCategory: UpdateExpensesCategory,
    val deleteExpensesCategory: DeleteExpensesCategory,
)
