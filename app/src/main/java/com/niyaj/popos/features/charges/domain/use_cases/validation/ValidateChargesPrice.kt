package com.niyaj.popos.features.charges.domain.use_cases.validation

import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateChargesPrice @Inject constructor(
    private val chargesValidationRepository: ChargesValidationRepository
) {

    operator fun invoke(doesApplicable: Boolean, chargesPrice: Int): ValidationResult {
        return chargesValidationRepository.validateChargesPrice(doesApplicable, chargesPrice)
    }
}