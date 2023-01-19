package com.niyaj.popos.features.expenses.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import javax.inject.Inject

class ValidateExpensesPrice @Inject constructor(
    private val expensesValidationRepository: ExpensesValidationRepository
) {

    operator fun invoke(expansesPrice: String): ValidationResult {
        return expensesValidationRepository.validateExpensesPrice(expansesPrice)
    }
}