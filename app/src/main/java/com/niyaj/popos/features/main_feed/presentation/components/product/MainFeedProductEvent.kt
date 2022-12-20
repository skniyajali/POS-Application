package com.niyaj.popos.features.main_feed.presentation.components.product

import com.niyaj.popos.features.product.domain.util.FilterProduct

sealed class MainFeedProductEvent{

    data class AddProductToCart(val orderId: String, val productId: String): MainFeedProductEvent()

    data class RemoveProductFromCart(val orderId: String, val productId: String): MainFeedProductEvent()

    data class OnFilterProduct(val filterProduct: FilterProduct): MainFeedProductEvent()

    data class SearchProduct(val searchText: String): MainFeedProductEvent()

    object ToggleSearchBar: MainFeedProductEvent()
}
