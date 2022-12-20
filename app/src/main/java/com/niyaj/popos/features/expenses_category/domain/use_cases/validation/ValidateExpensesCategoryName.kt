package com.niyaj.popos.features.expenses_category.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateExpensesCategoryName @Inject constructor() {

    fun execute(categoryName: String): ValidationResult {

        if(categoryName.isEmpty()) return ValidationResult(false, "Category name is empty")

        if (categoryName.length < 3) return ValidationResult(false, "Invalid category name")

        if (categoryName.any { it.isDigit() }) return ValidationResult(false, "Category name must not contain any digit")

        return ValidationResult(true)
    }
}