package com.niyaj.popos.features.customer.domain.model

data class CustomerWiseOrder(
    val cartOrderId: String,
    val orderId: String,
    val totalPrice: String,
    val updatedAt: String,
    val customerAddress: String? = null,
)
