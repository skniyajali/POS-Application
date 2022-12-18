package com.niyaj.popos.domain.use_cases.expenses.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateExpansesSubCategory @Inject constructor() {

    fun execute(subCategory: String): ValidationResult{

        if (subCategory.isEmpty()){
            return ValidationResult(
                false,
                "Sub Category is required",
            )
        }


        return ValidationResult(true)
    }
}