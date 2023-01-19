package com.niyaj.popos.features.address.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface AddressValidationRepository {

    fun validateAddressName(addressName: String, addressId: String? = null): ValidationResult

    fun validateAddressShortName(addressShortName: String): ValidationResult
}