package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    val categoryId: String = "",

    val categoryName: String = "",

    val categoryAvailability: Boolean = true,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<Category>.filterCategory(searchText: String): List<Category> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.categoryName.contains(searchText, true) ||
                    it.categoryAvailability.toString().contains(searchText, true) ||
                    it.createdAt.contains(searchText, true)
        }
    } else this
}