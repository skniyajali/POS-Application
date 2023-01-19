package com.niyaj.popos.features.address.domain.use_cases.validation

import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateAddressShortName @Inject constructor(
    private val addressValidationRepository: AddressValidationRepository
) {

    operator fun invoke(addressShortName: String): ValidationResult {
        return addressValidationRepository.validateAddressShortName(addressShortName)
    }
}