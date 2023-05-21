package com.niyaj.popos.features.charges.presentation

import com.niyaj.popos.features.charges.domain.model.Charges

data class ChargesState(
    val chargesItem: List<Charges> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
