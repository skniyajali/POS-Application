package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface CategoryValidationRepository {

    fun validateCategoryName(categoryName: String, categoryId: String? = null): ValidationResult
}