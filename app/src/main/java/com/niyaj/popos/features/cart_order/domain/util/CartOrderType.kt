package com.niyaj.popos.features.cart_order.domain.util

sealed class CartOrderType(val orderType: String){
    object DineIn : CartOrderType("DineIn")
    object DineOut : CartOrderType("DineOut")
}
