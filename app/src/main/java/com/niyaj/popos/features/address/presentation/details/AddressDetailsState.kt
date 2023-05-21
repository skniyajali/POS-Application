package com.niyaj.popos.features.address.presentation.details

import com.niyaj.popos.features.address.domain.model.AddressWiseOrder

data class AddressDetailsState(
    val orderDetails: List<AddressWiseOrder> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)


data class TotalOrderDetails(
    val totalAmount: String = "0",
    val totalOrder: Int = 0,
    val repeatedCustomer: Int = 0,
    val datePeriod: Pair<String, String> = Pair("", "")
)