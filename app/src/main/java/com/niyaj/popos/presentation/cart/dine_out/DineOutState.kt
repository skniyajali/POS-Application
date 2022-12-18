package com.niyaj.popos.presentation.cart.dine_out

import com.niyaj.popos.domain.model.Cart

data class DineOutState(
    val cartItems: List<Cart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
