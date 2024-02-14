package com.niyaj.feature.address.add_edit

sealed class AddEditAddressEvent {

    data class ShortNameChanged(val shortName: String) : AddEditAddressEvent()

    data class AddressNameChanged(val addressName: String) : AddEditAddressEvent()

    data object CreateOrUpdateAddress : AddEditAddressEvent()
}
