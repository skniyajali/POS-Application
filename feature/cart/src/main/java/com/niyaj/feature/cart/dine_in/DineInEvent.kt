package com.niyaj.feature.cart.dine_in

sealed class DineInEvent {

    data class IncreaseQuantity(val cartOrderId: String, val productId: String): DineInEvent()

    data class DecreaseQuantity(val cartOrderId: String, val productId: String): DineInEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String): DineInEvent()

    data class PlaceDineInOrder(val cartOrderId: String): DineInEvent()

    data object PlaceAllDineInOrder: DineInEvent()
}
