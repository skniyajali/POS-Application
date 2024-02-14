package com.niyaj.feature.cart.dine_in

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.niyaj.model.CartItem

@Stable
@Immutable
data class DineInState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = true,
)
