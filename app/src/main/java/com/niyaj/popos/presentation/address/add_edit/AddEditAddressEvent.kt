package com.niyaj.popos.presentation.address.add_edit

import com.niyaj.popos.presentation.address.AddressEvent

sealed class AddEditAddressEvent {

    data class ShortNameChanged(val shortName: String) : AddEditAddressEvent()

    data class AddressNameChanged(val addressName: String) : AddEditAddressEvent()

    object CreateNewAddress : AddEditAddressEvent()

    data class UpdateAddress(val addressId: String) : AddEditAddressEvent()
}
