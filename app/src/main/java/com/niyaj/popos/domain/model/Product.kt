package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.category.domain.model.Category
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val productId: String = "",
    val category: Category = Category(),
    val productName: String,
    val productPrice: Int,
    val productAvailability: Boolean = true,
    val created_at: String? = null,
    val updated_at: String? = null,
)