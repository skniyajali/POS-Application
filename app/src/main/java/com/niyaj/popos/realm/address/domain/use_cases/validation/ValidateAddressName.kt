package com.niyaj.popos.realm.address.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateAddressName @Inject constructor() {

    fun execute(address: String): ValidationResult {

        if(address.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address name must not be empty",
            )
        }

        if(address.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "The address must be more than 2 characters long"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}