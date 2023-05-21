package com.niyaj.popos.features.product.presentation

import com.niyaj.popos.features.product.domain.model.Product

data class ProductsState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
