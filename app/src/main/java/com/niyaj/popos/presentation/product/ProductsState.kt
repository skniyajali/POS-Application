package com.niyaj.popos.presentation.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterProduct

data class ProductsState(
    val products: List<Product> = emptyList(),
    val filterProduct: FilterProduct = FilterProduct.ByCategoryId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
