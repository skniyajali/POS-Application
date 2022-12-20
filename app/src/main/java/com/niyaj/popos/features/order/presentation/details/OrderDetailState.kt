package com.niyaj.popos.features.order.presentation.details

import com.niyaj.popos.features.cart.domain.model.Cart

data class OrderDetailState(
    val orderDetails: Cart? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
