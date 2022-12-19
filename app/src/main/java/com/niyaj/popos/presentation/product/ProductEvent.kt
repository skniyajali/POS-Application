package com.niyaj.popos.presentation.product

import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.domain.util.filter_items.FilterProduct

sealed class ProductEvent {

    data class DeleteProducts(val products: List<String>) : ProductEvent()

    data class SelectCategory(val categoryId: String) : ProductEvent()

    data class SelectProduct(val productId: String) : ProductEvent()

    data class SelectProducts(val products: List<String>) : ProductEvent()

    object SelectAllProduct : ProductEvent()

    object DeselectProducts : ProductEvent()

    data class OnFilterProduct(val filterProduct: FilterProduct): ProductEvent()

    data class OnSearchProduct(val searchText: String): ProductEvent()

    object ToggleSearchBar : ProductEvent()

    object RefreshProduct : ProductEvent()
}