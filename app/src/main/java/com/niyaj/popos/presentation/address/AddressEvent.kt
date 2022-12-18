package com.niyaj.popos.presentation.address

import com.niyaj.popos.domain.util.filter_items.FilterAddress

sealed class AddressEvent{

    data class SelectAddress(val addressId: String) : AddressEvent()

    object SelectAllAddress : AddressEvent()

    object DeselectAddress : AddressEvent()

    data class DeleteAddress(val addresses: List<String>) : AddressEvent()

    data class OnFilterAddress(val filterAddress: FilterAddress): AddressEvent()

    data class OnSearchAddress(val searchText: String): AddressEvent()

    object ToggleSearchBar : AddressEvent()

    object RefreshAddress : AddressEvent()
}
