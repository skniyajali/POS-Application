package com.niyaj.popos.features.product.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface ProductValidationRepository {
    fun validateCategoryName(categoryName: String): ValidationResult

    fun validateProductName(productName: String, productId: String? = null): ValidationResult

    fun validateProductPrice(productPrice: Int, type: String? = null): ValidationResult
}