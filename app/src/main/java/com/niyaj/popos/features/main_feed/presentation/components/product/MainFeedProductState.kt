package com.niyaj.popos.features.main_feed.presentation.components.product

import com.niyaj.popos.features.main_feed.domain.model.ProductWithFlowQuantity

data class MainFeedProductState(
    val products: List<ProductWithFlowQuantity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)