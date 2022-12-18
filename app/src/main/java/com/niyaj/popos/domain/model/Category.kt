package com.niyaj.popos.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryAvailability: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
