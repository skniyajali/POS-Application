package com.niyaj.popos.features.cart_order.domain.util

sealed class OrderStatus(val orderStatus: String){
    object Processing : OrderStatus("Processing")
    object Cancelled : OrderStatus("Cancelled")
    object Placed : OrderStatus("Placed")
    object Delivered : OrderStatus("Delivered")
}
