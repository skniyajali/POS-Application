package com.niyaj.popos.features.address.presentation

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.util.FilterAddress
import com.niyaj.popos.features.common.util.SortType

data class AddressState(
    val addresses: List<Address> = emptyList(),
    val filterAddress: FilterAddress = FilterAddress.ByAddressId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
