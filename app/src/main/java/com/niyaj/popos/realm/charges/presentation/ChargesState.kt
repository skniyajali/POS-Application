package com.niyaj.popos.realm.charges.presentation

import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.charges.domain.util.FilterCharges

data class ChargesState(
    val chargesItem: List<Charges> = emptyList(),
    val filterCharges: FilterCharges = FilterCharges.ByChargesId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
