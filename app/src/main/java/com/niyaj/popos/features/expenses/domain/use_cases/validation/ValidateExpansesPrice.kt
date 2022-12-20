package com.niyaj.popos.features.expenses.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateExpansesPrice @Inject constructor() {

    fun execute(expansesPrice: String): ValidationResult {

        if (expansesPrice.isEmpty()){
            return ValidationResult(
                false,
                "Expanses price must not be empty"
            )
        }

        if (expansesPrice.any { it.isLetter() }){
            return ValidationResult(
                false,
                "Expanses price must not contain a letter"
            )
        }

        if (expansesPrice.length > 6){
            return ValidationResult(
                false,
                "Invalid expanses price."
            )
        }


        return ValidationResult(true)
    }
}