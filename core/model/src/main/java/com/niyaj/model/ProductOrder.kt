package com.niyaj.model

data class ProductOrder(
    val cartOrderId: String = "",
    val orderId: String = "",
    val orderedDate: String = "",
    val orderType: OrderType = OrderType.DineIn,
    val quantity: Int = 0,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
)
