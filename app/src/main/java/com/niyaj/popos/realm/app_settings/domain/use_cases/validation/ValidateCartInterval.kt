package com.niyaj.popos.realm.app_settings.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import com.niyaj.popos.util.isContainsArithmeticCharacter
import javax.inject.Inject

class ValidateCartInterval @Inject constructor() {

    fun validate(cartsInterval: String): ValidationResult {

        if (cartsInterval.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval must not be empty"
            )
        }

        if (cartsInterval.isContainsArithmeticCharacter) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval is invalid"
            )
        }

        if (cartsInterval.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval must not contain a letter"
            )
        }

        try {
            if (cartsInterval.toInt() >= 15) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Cart interval must be between 15 days."
                )
            }
        }catch (e: Exception) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval is invalid"
            )
        }

        return ValidationResult(true)
    }
}