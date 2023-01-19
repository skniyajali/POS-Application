package com.niyaj.popos.features.category.domain.use_cases.validation

import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCategoryName @Inject constructor(
    private val categoryValidationRepository: CategoryValidationRepository
) {

    operator fun invoke(categoryName: String, categoryId: String?): ValidationResult {
        return categoryValidationRepository.validateCategoryName(categoryName, categoryId)
    }
}