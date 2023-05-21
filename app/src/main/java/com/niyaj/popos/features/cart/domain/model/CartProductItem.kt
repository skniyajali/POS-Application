package com.niyaj.popos.features.cart.domain.model

data class CartProductItem(
    val productId: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val productQuantity: Int = 0,
)
