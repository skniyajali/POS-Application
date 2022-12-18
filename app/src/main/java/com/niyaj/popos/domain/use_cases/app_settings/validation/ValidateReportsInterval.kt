package com.niyaj.popos.domain.use_cases.app_settings.validation

import com.niyaj.popos.domain.util.ValidationResult
import com.niyaj.popos.util.isContainsArithmeticCharacter
import javax.inject.Inject

class ValidateReportsInterval @Inject constructor() {

    fun validate(reportsInterval: String): ValidationResult {

        if (reportsInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval must not be empty"
            )
        }

        if (reportsInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval is invalid"
            )
        }

        if (reportsInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval must not contain a letter"
            )
        }

        try {
            if (reportsInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Reports interval must be between 7-15 days."
                )
            }

            if (reportsInterval.toInt() < 7) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Reports interval must be between 7-15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Reports interval is invalid"
            )
        }

        return ValidationResult(true)
    }
}