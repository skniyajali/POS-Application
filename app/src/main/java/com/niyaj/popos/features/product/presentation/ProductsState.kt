package com.niyaj.popos.features.product.presentation

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.util.FilterProduct

data class ProductsState(
    val products: List<Product> = emptyList(),
    val filterProduct: FilterProduct = FilterProduct.ByCategoryId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
