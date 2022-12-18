package com.niyaj.popos.domain.use_cases.charges.validation

import com.niyaj.popos.domain.use_cases.charges.ChargesUseCases
import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateChargesName @Inject constructor(
    private val chargesUseCases: ChargesUseCases
) {

    fun execute(chargesName: String, chargesId: String?): ValidationResult{

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