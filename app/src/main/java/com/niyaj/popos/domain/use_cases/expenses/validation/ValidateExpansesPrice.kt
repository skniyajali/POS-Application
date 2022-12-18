package com.niyaj.popos.domain.use_cases.expenses.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateExpansesPrice @Inject constructor() {

    fun execute(expansesPrice: String): ValidationResult{

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