package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface AddOnItemValidationRepository {

    fun validateItemName(name: String, addOnItemId: String?): ValidationResult

    fun validateItemPrice(price: Int): ValidationResult
}