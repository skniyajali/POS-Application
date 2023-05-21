package com.niyaj.popos.features.cart.presentation.dine_out

import com.niyaj.popos.features.cart.domain.model.CartItem

data class DineOutState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
