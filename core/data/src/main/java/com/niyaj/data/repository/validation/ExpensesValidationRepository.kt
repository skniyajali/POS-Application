package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface ExpensesValidationRepository {

    fun validateExpensesCategory(categoryId: String): ValidationResult

    fun validateExpenseDate(expenseDate: String): ValidationResult

    fun validateExpensesPrice(expansesPrice: String): ValidationResult
}