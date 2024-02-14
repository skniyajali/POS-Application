package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Customer(
    val customerId: String = "",

    val customerPhone: String = "",

    val customerName: String? = null,

    val customerEmail: String? = null,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<Customer>.filterCustomer(searchText: String): List<Customer> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.customerEmail?.contains(searchText, true) == true ||
                    it.customerPhone.contains(searchText, true) ||
                    it.customerName?.contains(searchText, true) == true
        }
    } else this
}