package com.niyaj.popos.features.order.domain.model

import com.niyaj.popos.features.cart.domain.model.CartProductItem
import com.niyaj.popos.features.cart_order.domain.model.CartOrder

data class OrderDetail(
    val cartOrder: CartOrder? = null,
    val orderedProducts: List<CartProductItem> = emptyList(),
    val orderPrice: Pair<Int, Int> = Pair(0,0)
)
