package com.niyaj.feature.cart.dine_out

import com.niyaj.model.CartItem

data class DineOutState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
)
