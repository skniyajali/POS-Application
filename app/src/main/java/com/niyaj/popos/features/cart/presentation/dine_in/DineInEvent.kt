package com.niyaj.popos.features.cart.presentation.dine_in

sealed class DineInEvent {

    object GetAllDineInOrders: DineInEvent()

    data class AddProductToCart(val cartOrderId: String, val productId: String): DineInEvent()

    data class RemoveProductFromCart(val cartOrderId: String, val productId: String): DineInEvent()

    data class UpdateAddOnItemInCart(val addOnItemId: String, val cartOrderId: String): DineInEvent()

    data class SelectDineInOrder(val cartOrderId: String): DineInEvent()

    data class PlaceDineInOrder(val cartOrderId: String): DineInEvent()

    object PlaceAllDineInOrder: DineInEvent()

    object SelectAllDineInOrder: DineInEvent()

    object RefreshDineInOrder: DineInEvent()
}
