package com.niyaj.popos.domain.util

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
