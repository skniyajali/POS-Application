package com.niyaj.popos.domain.model

data class Order(
    val orderId: String,
    val cart: Cart,
    val created_at: String? = null,
    val updated_at: String? = null,
)