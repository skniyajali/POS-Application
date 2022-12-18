package com.niyaj.popos.domain.model

data class CartProduct(
    val cartProductId: String? = null,
    val orderId: String? = null,
    val product: Product? = null,
    val quantity: Int? = 0,
)
