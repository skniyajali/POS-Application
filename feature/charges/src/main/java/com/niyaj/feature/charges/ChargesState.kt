package com.niyaj.feature.charges

data class ChargesState(
    val chargesItem: List<com.niyaj.database.model.ChargesEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
