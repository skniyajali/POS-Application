package com.niyaj.popos.features.address.domain.use_cases

data class AddressUseCases(
    val getAllAddress: GetAllAddress,
    val getAddressById: GetAddressById,
    val createNewAddress: CreateNewAddress,
    val updateAddress: UpdateAddress,
    val deleteAddress: DeleteAddress,
)
