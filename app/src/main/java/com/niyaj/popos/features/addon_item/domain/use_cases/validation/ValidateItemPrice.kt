package com.niyaj.popos.features.addon_item.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
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