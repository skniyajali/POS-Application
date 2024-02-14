package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface ExpCategoryValidationRepository {
    fun validateExpensesCategoryName(categoryName: String): ValidationResult
}