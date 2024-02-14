package com.niyaj.feature.cart.dine_out

sealed class DineOutEvent {

    data class IncreaseQuantity(val orderId: String, val productId: String) : DineOutEvent()

    data class DecreaseQuantity(val orderId: String, val productId: String) : DineOutEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String) :
        DineOutEvent()

    data class PlaceDineOutOrder(val cartId: String) : DineOutEvent()

    data object PlaceAllDineOutOrder : DineOutEvent()
}
