package com.niyaj.popos.presentation.cart.dine_out

sealed class DineOutEvent {

    object GetAllDineOutOrders: DineOutEvent()

    data class AddProductToCart(val orderId: String, val productId: String): DineOutEvent()

    data class RemoveProductFromCart(val orderId: String, val productId: String): DineOutEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String): DineOutEvent()

    data class SelectDineOutOrder(val cartId: String): DineOutEvent()

    data class PlaceDineOutOrder(val cartId: String): DineOutEvent()

    object PlaceAllDineOutOrder: DineOutEvent()

    object SelectAllDineOutOrder: DineOutEvent()

    object RefreshDineOutOrder: DineOutEvent()
}
