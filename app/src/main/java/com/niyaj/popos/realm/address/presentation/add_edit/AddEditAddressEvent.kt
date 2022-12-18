package com.niyaj.popos.realm.address.presentation.add_edit

sealed class AddEditAddressEvent {

    data class ShortNameChanged(val shortName: String) : AddEditAddressEvent()

    data class AddressNameChanged(val addressName: String) : AddEditAddressEvent()

    object CreateNewAddress : AddEditAddressEvent()

    data class UpdateAddress(val addressId: String) : AddEditAddressEvent()
}
