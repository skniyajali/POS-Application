package com.niyaj.popos.features.charges.domain.use_cases.validation

import com.niyaj.popos.features.charges.domain.use_cases.ChargesUseCases
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateChargesName @Inject constructor(
    private val chargesUseCases: ChargesUseCases
) {

    fun execute(chargesName: String, chargesId: String?): ValidationResult {

        if(chargesName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must not be empty",
            )
        }

        if(chargesName.length < 5 ){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must be more than 5 characters long",
            )
        }

        val result = chargesName.any { it.isDigit() }

        if (result){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name must not contain a digit",
            )
        }

        if (chargesUseCases.findChargesByName(chargesName, chargesId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Charges Name already exists.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}