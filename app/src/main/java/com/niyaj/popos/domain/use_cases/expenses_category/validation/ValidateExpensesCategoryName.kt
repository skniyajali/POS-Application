package com.niyaj.popos.domain.use_cases.expenses_category.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateExpensesCategoryName @Inject constructor() {

    fun execute(categoryName: String): ValidationResult{

        if(categoryName.isEmpty()) return ValidationResult(false, "Category name is empty")

        if (categoryName.length < 3) return ValidationResult(false, "Invalid category name")

        if (categoryName.any { it.isDigit() }) return ValidationResult(false, "Category name must not contain any digit")

        return ValidationResult(true)
    }
}