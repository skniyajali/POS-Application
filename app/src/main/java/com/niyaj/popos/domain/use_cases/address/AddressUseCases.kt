package com.niyaj.popos.domain.use_cases.address

data class AddressUseCases(
    val getAllAddress: GetAllAddress,
    val getAddressById: GetAddressById,
    val createNewAddress: CreateNewAddress,
    val updateAddress: UpdateAddress,
    val deleteAddress: DeleteAddress,
)
