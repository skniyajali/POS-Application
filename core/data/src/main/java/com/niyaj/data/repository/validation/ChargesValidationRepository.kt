package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface ChargesValidationRepository {

    fun validateChargesName(chargesName: String, chargesId: String? = null): ValidationResult

    fun validateChargesPrice(chargesPrice: Int): ValidationResult
}