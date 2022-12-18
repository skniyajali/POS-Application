package com.niyaj.popos.domain.util

sealed class CartOrderType(val orderType: String){
    object DineIn : CartOrderType("DineIn")
    object DineOut : CartOrderType("DineOut")
}
