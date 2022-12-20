package com.niyaj.popos.features.cart_order.presentation

import com.niyaj.popos.features.cart_order.domain.model.CartOrder

data class CartOrderState(
    val cartOrders: List<CartOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
