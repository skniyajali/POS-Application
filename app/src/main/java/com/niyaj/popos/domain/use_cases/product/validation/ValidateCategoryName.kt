package com.niyaj.popos.domain.use_cases.product.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateCategoryName @Inject constructor() {

    fun execute(categoryName: String): ValidationResult {
        if(categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product Category required",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}