package com.niyaj.model

data class OrderDetail(
    val cartOrder: CartOrder? = null,
    val orderedProducts: List<CartProductItem> = emptyList(),
    val orderPrice: Pair<Int, Int> = Pair(0,0)
)
