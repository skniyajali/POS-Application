package com.niyaj.popos.features.cart.presentation.dine_in

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.niyaj.popos.features.cart.domain.model.CartItem

@Stable
@Immutable
data class DineInState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
