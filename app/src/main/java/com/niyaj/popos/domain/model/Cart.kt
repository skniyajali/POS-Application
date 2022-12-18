package com.niyaj.popos.domain.model

data class Cart(
    val cartOrder: CartOrder? = null,
    val cartProducts: List<CartProduct> = emptyList(),
    val orderPrice: Pair<Int, Int> = Pair(0,0)
)