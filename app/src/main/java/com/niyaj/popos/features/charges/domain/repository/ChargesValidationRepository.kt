package com.niyaj.popos.features.charges.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface ChargesValidationRepository {

    fun validateChargesName(chargesName: String, chargesId: String? = null): ValidationResult

    fun validateChargesPrice(doesApplicable: Boolean, chargesPrice: Int): ValidationResult
}