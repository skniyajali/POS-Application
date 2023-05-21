package com.niyaj.popos.features.cart.presentation.dine_out

sealed class DineOutEvent {

    object GetAllDineOutOrders: DineOutEvent()

    data class IncreaseQuantity(val orderId: String, val productId: String): DineOutEvent()

    data class DecreaseQuantity(val orderId: String, val productId: String): DineOutEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String): DineOutEvent()

    data class SelectDineOutOrder(val cartId: String): DineOutEvent()

    data class PlaceDineOutOrder(val cartId: String): DineOutEvent()

    object PlaceAllDineOutOrder: DineOutEvent()

    object SelectAllDineOutOrder: DineOutEvent()

    object RefreshDineOutOrder: DineOutEvent()
}
