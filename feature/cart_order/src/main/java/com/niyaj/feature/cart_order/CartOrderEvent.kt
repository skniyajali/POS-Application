package com.niyaj.feature.cart_order

sealed class CartOrderEvent{

    data class DeleteCartOrder(val cartOrderId: String): CartOrderEvent()

    data class SelectCartOrderEvent(val cartOrderId: String) : CartOrderEvent()
}
