package com.niyaj.popos.features.main_feed.presentation.components.product

sealed class MainFeedProductEvent{

    data class AddProductToCart(val cartOrderId: String, val productId: String): MainFeedProductEvent()

    data class RemoveProductFromCart(val cartOrderId: String, val productId: String): MainFeedProductEvent()
    
    data class SearchProduct(val searchText: String): MainFeedProductEvent()

    object ToggleSearchBar: MainFeedProductEvent()
}
