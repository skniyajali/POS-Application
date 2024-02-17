package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface ExpCategoryValidationRepository {
    suspend fun validateName(categoryName: String, categoryId: String?): ValidationResult
}