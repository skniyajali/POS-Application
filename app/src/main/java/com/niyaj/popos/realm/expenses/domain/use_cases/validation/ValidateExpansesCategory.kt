package com.niyaj.popos.realm.expenses.domain.use_cases.validation

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