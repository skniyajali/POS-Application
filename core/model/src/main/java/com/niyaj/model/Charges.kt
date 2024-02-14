package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Charges(
    val chargesId: String = "",

    val chargesName: String = "",

    val chargesPrice: Int = 0,

    val isApplicable: Boolean = false,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)

fun List<Charges>.filterCharges(searchText: String): List<Charges> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.chargesName.contains(searchText, true) ||
                    it.chargesPrice.toString().contains(searchText, true) ||
                    it.createdAt.contains(searchText, true) ||
                    it.updatedAt?.contains(searchText, true) == true
        }
    } else this
}