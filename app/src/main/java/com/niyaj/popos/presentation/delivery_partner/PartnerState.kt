package com.niyaj.popos.presentation.delivery_partner

import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterPartner

data class PartnerState(
    val partners: List<DeliveryPartner> = emptyList(),
    val filterPartner: FilterPartner = FilterPartner.ByPartnerId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
