package com.niyaj.popos.features.cart.domain.model

import com.niyaj.popos.features.cart_order.domain.model.CartOrder

data class Cart(
    val cartOrder: CartOrder? = null,
    val cartProducts: List<CartProduct> = emptyList(),
    val orderPrice: Pair<Int, Int> = Pair(0,0)
)