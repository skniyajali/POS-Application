package com.niyaj.popos.domain.model

data class Charges(
    val chargesId: String = "",
    val chargesName: String = "",
    val chargesPrice: Int = 0,
    val isApplicable: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
