package com.niyaj.popos.presentation.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterCharges

data class ChargesState(
    val chargesItem: List<Charges> = emptyList(),
    val filterCharges: FilterCharges = FilterCharges.ByChargesId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
