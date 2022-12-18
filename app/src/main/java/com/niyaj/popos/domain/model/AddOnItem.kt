package com.niyaj.popos.domain.model

data class AddOnItem(
    val addOnItemId: String = "",
    val itemName: String,
    val itemPrice: Int,
    val created_at: String? = null,
    val updated_at: String? = null,
)
