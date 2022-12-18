package com.niyaj.popos.presentation.order.details

import com.niyaj.popos.domain.model.Cart

data class OrderDetailState(
    val orderDetails: Cart? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
