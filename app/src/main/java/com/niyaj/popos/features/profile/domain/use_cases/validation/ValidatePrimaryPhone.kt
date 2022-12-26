package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidatePrimaryPhone @Inject constructor() {

    fun validate(primaryPhone: String): ValidationResult {

        if (primaryPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone must not be empty"
            )
        }

        if (primaryPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone should not contain any characters"
            )
        }

        if (primaryPhone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant primary phone must be 10 digits"
            )
        }

        return ValidationResult(true)
    }
}