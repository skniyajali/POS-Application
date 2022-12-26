package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateSecondaryPhone @Inject constructor() {

    fun validate(secondaryPhone: String): ValidationResult {

        if (secondaryPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone must not be empty"
            )
        }

        if (secondaryPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone should not contain any characters"
            )
        }

        if (secondaryPhone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant secondary phone must be 10 digits"
            )
        }

        return ValidationResult(true)
    }
}