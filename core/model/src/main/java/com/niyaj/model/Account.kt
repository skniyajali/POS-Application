package com.niyaj.model

data class Account(
    val restaurantId: String = RESTAURANT_ID,

    val email: String = "",

    val phone: String = "",

    val password: String = "",

    val isLoggedIn: Boolean = true,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)
