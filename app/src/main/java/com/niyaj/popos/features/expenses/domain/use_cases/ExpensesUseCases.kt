package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.expenses.domain.use_cases.validation.ValidateExpensesCategory
import com.niyaj.popos.features.expenses.domain.use_cases.validation.ValidateExpensesPrice

data class ExpensesUseCases(
    val validateExpensesCategory: ValidateExpensesCategory,
    val validateExpensesPrice: ValidateExpensesPrice,
    val getAllExpenses: GetAllExpenses,
    val getExpensesById: GetExpensesById,
    val createNewExpenses: CreateNewExpenses,
    val updateExpenses: UpdateExpenses,
    val deleteExpenses: DeleteExpenses,
    val deletePastExpenses: DeletePastExpenses,
)
