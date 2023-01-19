package com.niyaj.popos.features.address.domain.use_cases

import com.niyaj.popos.features.address.domain.use_cases.validation.ValidateAddressName
import com.niyaj.popos.features.address.domain.use_cases.validation.ValidateAddressShortName

data class AddressUseCases(
    val validateAddressName: ValidateAddressName,
    val validateAddressShortName: ValidateAddressShortName,
    val getAllAddress: GetAllAddress,
    val getAddressById: GetAddressById,
    val createNewAddress: CreateNewAddress,
    val updateAddress: UpdateAddress,
    val deleteAddress: DeleteAddress,
)
