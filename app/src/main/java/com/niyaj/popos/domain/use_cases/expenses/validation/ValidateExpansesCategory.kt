package com.niyaj.popos.domain.use_cases.expenses.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateExpansesCategory @Inject constructor() {

    fun execute(categoryId: String): ValidationResult{

        if (categoryId.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Category is required",
            )
        }

        return ValidationResult(true)
    }
}