package com.niyaj.popos.domain.use_cases.product.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateProductPrice @Inject constructor() {

    fun execute(productPrice: Int, type: String? = null): ValidationResult {
        if(productPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product price required.",
            )
        }

        if(type.isNullOrEmpty() && productPrice <= 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product price must be larger than 10 rupees."
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}