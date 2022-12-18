package com.niyaj.popos.realm.address.presentation

import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.address.domain.util.FilterAddress
import com.niyaj.popos.realm.address.domain.model.Address

data class AddressState(
    val addresses: List<Address> = emptyList(),
    val filterAddress: FilterAddress = FilterAddress.ByAddressId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
