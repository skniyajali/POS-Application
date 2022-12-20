package com.niyaj.popos.features.product.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
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