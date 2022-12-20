package com.niyaj.popos.features.main_feed.domain.model

import com.niyaj.popos.features.product.domain.model.Product

data class ProductWithQuantity(
    val product: Product,
    val quantity: Int = 0
)
