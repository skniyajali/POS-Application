package com.niyaj.model

data class Cart(
    val cartId: String,

    val cartOrder: CartOrder? = null,

    val product: Product? = null,

    val quantity: Int = 0,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)