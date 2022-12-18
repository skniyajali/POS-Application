package com.niyaj.popos.domain.model

data class Customer(
    val customerId: String = "",
    val customerPhone: String = "",
    val customerName: String? = null,
    val customerEmail: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
)
