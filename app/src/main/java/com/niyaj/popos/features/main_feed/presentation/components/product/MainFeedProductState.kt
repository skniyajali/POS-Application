package com.niyaj.popos.features.main_feed.presentation.components.product

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.product.domain.util.FilterProduct

data class MainFeedProductState(
    val products: List<ProductWithQuantity> = emptyList(),
    val filterProduct: FilterProduct = FilterProduct.ByProductId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)