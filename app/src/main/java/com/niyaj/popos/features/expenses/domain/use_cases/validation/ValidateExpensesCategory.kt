package com.niyaj.popos.features.expenses.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import javax.inject.Inject

class ValidateExpensesCategory @Inject constructor(
    private val expensesValidationRepository: ExpensesValidationRepository
) {

    operator fun invoke(categoryId: String): ValidationResult {
        return expensesValidationRepository.validateExpensesCategory(categoryId)
    }
}