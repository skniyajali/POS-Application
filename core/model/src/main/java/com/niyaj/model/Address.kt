package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Address(
    val addressId: String = "",

    val shortName: String = "",

    val addressName: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<Address>.filterAddress(searchText: String): List<Address> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.shortName.contains(searchText, true) ||
                    it.addressName.contains(searchText, true)
        }
    } else this
}