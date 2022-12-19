package com.niyaj.popos.realm.app_settings.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import com.niyaj.popos.util.isContainsArithmeticCharacter
import javax.inject.Inject

class ValidateExpensesInterval @Inject constructor() {

    fun validate(expensesInterval: String): ValidationResult {

        if (expensesInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval must not be empty"
            )
        }

        if (expensesInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval is invalid"
            )
        }

        if (expensesInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval must not contain a letter"
            )
        }

        try {
            if (expensesInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Expenses interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expenses interval is invalid"
            )
        }


        return ValidationResult(true)
    }
}