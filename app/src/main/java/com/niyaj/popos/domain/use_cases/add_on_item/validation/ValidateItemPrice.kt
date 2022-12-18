package com.niyaj.popos.domain.use_cases.add_on_item.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateItemPrice @Inject constructor() {

    fun execute(price: Int): ValidationResult {

        if(price == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item price must not be empty",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}