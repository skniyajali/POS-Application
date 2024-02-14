package com.niyaj.feature.product.add_edit

data class AddEditProductState(
    val productName: String = "",
    val productPrice: String = "",
    val productAvailability: Boolean = true,
)

