package com.niyaj.model

data class CartItem(
    val cartOrderId: String = "",
    val orderId: String = "",
    val orderType: OrderType = OrderType.DineIn,
    val cartProducts: List<CartProductItem> = emptyList(),
    val addOnItems: List<String> = emptyList(),
    val customerPhone: String? = null,
    val customerAddress: String? = null,
    val updatedAt: String = "",
    val orderPrice : Pair<Int, Int> = Pair(0,0),
)

