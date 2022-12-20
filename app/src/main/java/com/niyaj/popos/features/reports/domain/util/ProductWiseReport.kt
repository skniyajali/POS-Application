package com.niyaj.popos.features.reports.domain.util

import com.niyaj.popos.features.product.domain.model.Product

data class ProductWiseReport(
    val product: Product? = null,
    val quantity: Int = 0
)
