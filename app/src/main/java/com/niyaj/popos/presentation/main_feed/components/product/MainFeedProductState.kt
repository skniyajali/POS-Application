package com.niyaj.popos.presentation.main_feed.components.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterProduct

data class MainFeedProductState(
    val products: List<ProductWithQuantity> = emptyList(),
    val filterProduct: FilterProduct = FilterProduct.ByProductId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ProductWithQuantity(
    val product: Product,
    val quantity: Int = 0
)