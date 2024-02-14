package com.niyaj.feature.home.components.product

sealed class MainFeedProductEvent{

    data class AddProductToCart(val cartOrderId: String, val productId: String): MainFeedProductEvent()

    data class RemoveProductFromCart(val cartOrderId: String, val productId: String): MainFeedProductEvent()
    
    data class SearchProduct(val searchText: String): MainFeedProductEvent()

    data object ToggleSearchBar: MainFeedProductEvent()
}
