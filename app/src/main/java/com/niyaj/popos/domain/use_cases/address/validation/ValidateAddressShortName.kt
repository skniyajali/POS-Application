package com.niyaj.popos.domain.use_cases.address.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateAddressShortName @Inject constructor() {

    fun execute(shortName: String): ValidationResult {

        if(shortName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address short name cannot be empty"
            )
        }

        if(shortName.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The short name must be more than 2 characters long"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}