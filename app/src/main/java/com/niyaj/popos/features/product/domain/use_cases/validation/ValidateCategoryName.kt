package com.niyaj.popos.features.product.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import javax.inject.Inject

class ValidateCategoryName @Inject constructor(
    private val productValidationRepository: ProductValidationRepository
) {

    operator fun invoke(categoryName: String): ValidationResult {
        return productValidationRepository.validateCategoryName(categoryName)
    }
}