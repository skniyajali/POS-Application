package com.niyaj.popos.features.cart_order.presentation

sealed class CartOrderEvent{

    data class DeleteCartOrder(val cartOrderId: String): CartOrderEvent()

    data class SelectCartOrderEvent(val cartOrderId: String) : CartOrderEvent()

    data class SelectCartOrder(val cartOrderId: String) : CartOrderEvent()

    data class OnSearchCartOrder(val searchText: String): CartOrderEvent()

    object ToggleSearchBar : CartOrderEvent()

    object RefreshCartOrder: CartOrderEvent()

    object ViewAllOrders: CartOrderEvent()
}
