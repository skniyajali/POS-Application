package com.niyaj.popos.presentation.main_feed.components.product

import com.niyaj.popos.domain.util.filter_items.FilterProduct
import com.niyaj.popos.presentation.cart.dine_out.DineOutEvent

sealed class MainFeedProductEvent{

    data class AddProductToCart(val orderId: String, val productId: String): MainFeedProductEvent()

    data class RemoveProductFromCart(val orderId: String, val productId: String): MainFeedProductEvent()

    data class OnFilterProduct(val filterProduct: FilterProduct): MainFeedProductEvent()

    data class SearchProduct(val searchText: String): MainFeedProductEvent()

    object ToggleSearchBar: MainFeedProductEvent()
}
