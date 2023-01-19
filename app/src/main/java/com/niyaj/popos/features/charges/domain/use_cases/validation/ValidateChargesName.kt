package com.niyaj.popos.features.charges.domain.use_cases.validation

import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateChargesName @Inject constructor(
    private val chargesValidationRepository: ChargesValidationRepository
) {

    operator fun invoke(chargesName: String, chargesId: String?): ValidationResult {
        return chargesValidationRepository.validateChargesName(chargesName, chargesId)
    }
}