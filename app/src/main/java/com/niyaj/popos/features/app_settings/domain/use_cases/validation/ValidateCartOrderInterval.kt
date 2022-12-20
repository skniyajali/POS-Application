package com.niyaj.popos.features.app_settings.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.util.isContainsArithmeticCharacter
import javax.inject.Inject

class ValidateCartOrderInterval @Inject constructor() {

    fun validate(cartOrderInterval: String): ValidationResult {

        if (cartOrderInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval must not be empty"
            )
        }

        if (cartOrderInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval is invalid"
            )
        }

        if (cartOrderInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval must not contain a letter"
            )
        }

        try {
            if (cartOrderInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "CartOrder interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "CartOrder interval is invalid"
            )
        }

        return ValidationResult(true)
    }
}