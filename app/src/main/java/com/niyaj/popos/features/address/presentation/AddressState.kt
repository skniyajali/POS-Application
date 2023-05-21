package com.niyaj.popos.features.address.presentation

import com.niyaj.popos.features.address.domain.model.Address

data class AddressState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)