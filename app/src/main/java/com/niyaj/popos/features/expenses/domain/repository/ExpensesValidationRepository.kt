package com.niyaj.popos.features.expenses.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface ExpensesValidationRepository {

    fun validateExpensesCategory(categoryId: String): ValidationResult

    fun validateExpensesPrice(expansesPrice: String): ValidationResult
}