package com.niyaj.popos.features.address.domain.use_cases.validation

import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateAddressName @Inject constructor(
    private val addressValidationRepository: AddressValidationRepository
) {

    operator fun invoke(addressName: String, addressId: String? = null): ValidationResult {
        return addressValidationRepository.validateAddressName(addressName, addressId)
    }
}