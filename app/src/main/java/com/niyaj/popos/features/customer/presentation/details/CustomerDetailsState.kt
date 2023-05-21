package com.niyaj.popos.features.customer.presentation.details

import com.niyaj.popos.features.customer.domain.model.CustomerWiseOrder

data class CustomerDetailsState(
    val orderDetails: List<CustomerWiseOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


data class TotalCustomerOrderDetails(
    val totalAmount: String = "0",
    val totalOrder: Int = 0,
    val repeatedOrder: Int = 0,
    val datePeriod: Pair<String, String> = Pair("", "")
)