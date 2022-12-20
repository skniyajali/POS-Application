package com.niyaj.popos.features.common.util

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)
