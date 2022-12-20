package com.niyaj.popos.features.cart.domain.model

import com.niyaj.popos.features.product.domain.model.Product

data class CartProduct(
    val cartProductId: String? = null,
    val orderId: String? = null,
    val product: Product? = null,
    val quantity: Int? = 0,
)
