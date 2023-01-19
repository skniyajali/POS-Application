package com.niyaj.popos.features.category.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface CategoryValidationRepository {

    fun validateCategoryName(categoryName: String, categoryId: String? = null): ValidationResult
}