package com.niyaj.popos.features.cart.presentation.dine_in

import com.niyaj.popos.features.cart.domain.model.Cart

data class DineInState(
    val cartItems: List<Cart> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
