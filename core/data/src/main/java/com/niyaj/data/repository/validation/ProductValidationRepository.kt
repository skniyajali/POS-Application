package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface ProductValidationRepository {
    fun validateCategoryName(categoryName: String): ValidationResult

    fun validateProductName(productName: String, productId: String? = null): ValidationResult

    fun validateProductPrice(productPrice: Int): ValidationResult
}