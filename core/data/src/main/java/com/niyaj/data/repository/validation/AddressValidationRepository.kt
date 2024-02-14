package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface AddressValidationRepository {

    fun validateAddressName(addressName: String, addressId: String? = null): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}