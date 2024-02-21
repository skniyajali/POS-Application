package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface CategoryValidationRepository {

    suspend fun validateCategoryName(
        categoryName: String,
        categoryId: String? = null
    ): ValidationResult
}