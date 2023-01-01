package com.niyaj.popos.features.customer.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Contact(
    val contactId: String,
    val phoneNo: String,
    val name: String? = null,
    val email: String? = null,
)
