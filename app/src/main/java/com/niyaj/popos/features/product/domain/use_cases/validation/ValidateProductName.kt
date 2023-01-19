package com.niyaj.popos.features.product.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import javax.inject.Inject

class ValidateProductName @Inject constructor(
    private val productValidationRepository: ProductValidationRepository
) {

    operator fun invoke(productName: String, productId: String? = null): ValidationResult {
        return productValidationRepository.validateProductName(productName, productId)
    }
}