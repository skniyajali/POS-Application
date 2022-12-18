package com.niyaj.popos.presentation.cart.dine_in

import com.niyaj.popos.domain.model.Cart

data class DineInState(
    val cartItems: List<Cart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
