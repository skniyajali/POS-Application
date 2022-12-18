package com.niyaj.popos.presentation.cart.dine_in

sealed class DineInEvent {

    object GetAllDineInOrders: DineInEvent()

    data class AddProductToCart(val orderId: String, val productId: String): DineInEvent()

    data class RemoveProductFromCart(val orderId: String, val productId: String): DineInEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String): DineInEvent()

    data class SelectDineInOrder(val cartId: String): DineInEvent()

    data class PlaceDineInOrder(val cartId: String): DineInEvent()

    object PlaceAllDineInOrder: DineInEvent()

    object SelectAllDineInOrder: DineInEvent()

    object RefreshDineInOrder: DineInEvent()
}
