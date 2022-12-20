package com.niyaj.popos.features.cart.presentation.dine_out

import com.niyaj.popos.features.cart.domain.model.Cart

data class DineOutState(
    val cartItems: List<Cart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
