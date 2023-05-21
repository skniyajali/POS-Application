package com.niyaj.popos.features.order.presentation.details

import com.niyaj.popos.features.order.domain.model.OrderDetail

data class OrderDetailState(
    val orderDetails: OrderDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
