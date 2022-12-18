package com.niyaj.popos.realm.address.domain.use_cases

data class AddressUseCases(
    val getAllAddress: GetAllAddress,
    val getAddressById: GetAddressById,
    val createNewAddress: CreateNewAddress,
    val updateAddress: UpdateAddress,
    val deleteAddress: DeleteAddress,
)
