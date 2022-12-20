package com.niyaj.popos.features.expenses.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateExpansesSubCategory @Inject constructor() {

    fun execute(subCategory: String): ValidationResult {

        if (subCategory.isEmpty()){
            return ValidationResult(
                false,
                "Sub Category is required",
            )
        }


        return ValidationResult(true)
    }
}