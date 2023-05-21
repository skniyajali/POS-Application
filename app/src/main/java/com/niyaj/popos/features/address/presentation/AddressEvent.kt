package com.niyaj.popos.features.address.presentation

sealed class AddressEvent{

    data class SelectAddress(val addressId: String) : AddressEvent()

    object SelectAllAddress : AddressEvent()

    object DeselectAddress : AddressEvent()

    data class DeleteAddress(val addresses: List<String>) : AddressEvent()

    data class OnSearchAddress(val searchText: String): AddressEvent()

    object ToggleSearchBar : AddressEvent()

    object RefreshAddress : AddressEvent()
}
