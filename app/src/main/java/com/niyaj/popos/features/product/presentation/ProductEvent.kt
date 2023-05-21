package com.niyaj.popos.features.product.presentation

import com.niyaj.popos.features.product.presentation.components.ViewType

sealed class ProductEvent {

    data class DeleteProducts(val products: List<String>) : ProductEvent()

    data class SelectCategory(val categoryId: String) : ProductEvent()

    data class SelectProduct(val productId: String) : ProductEvent()

    data class SelectProducts(val products: List<String>) : ProductEvent()

    data class OnSearchProduct(val searchText: String): ProductEvent()

    data class OnChangeViewType(val viewType: ViewType): ProductEvent()

    object SelectAllProduct : ProductEvent()

    object DeselectProducts : ProductEvent()

    object ToggleSearchBar : ProductEvent()

    object RefreshProduct : ProductEvent()
}