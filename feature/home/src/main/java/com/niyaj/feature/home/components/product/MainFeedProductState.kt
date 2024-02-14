package com.niyaj.feature.home.components.product

import com.niyaj.model.ProductWithFlowQuantity

data class MainFeedProductState(
    val products: List<ProductWithFlowQuantity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)