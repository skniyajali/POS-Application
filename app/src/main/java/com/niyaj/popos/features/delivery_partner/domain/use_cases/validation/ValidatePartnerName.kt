package com.niyaj.popos.features.delivery_partner.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerValidationRepository
import javax.inject.Inject

class ValidatePartnerName @Inject constructor(
    private val partnerValidationRepository: PartnerValidationRepository
) {

    operator fun invoke(partnerName: String): ValidationResult {
        return partnerValidationRepository.validatePartnerName(partnerName)
    }
}