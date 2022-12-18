package com.niyaj.popos.presentation.cart_order

import com.niyaj.popos.domain.model.CartOrder

data class CartOrderState(
    val cartOrders: List<CartOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
