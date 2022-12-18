package com.niyaj.popos.presentation.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterAddress

data class AddressState(
    val addresses: List<Address> = emptyList(),
    val filterAddress: FilterAddress = FilterAddress.ByAddressId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
