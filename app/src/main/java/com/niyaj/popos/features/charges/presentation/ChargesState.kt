package com.niyaj.popos.features.charges.presentation

import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.util.FilterCharges
import com.niyaj.popos.features.common.util.SortType

data class ChargesState(
    val chargesItem: List<Charges> = emptyList(),
    val filterCharges: FilterCharges = FilterCharges.ByChargesId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
