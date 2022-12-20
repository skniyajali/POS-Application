package com.niyaj.popos.features.delivery_partner.presentation

import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.util.FilterPartner

data class PartnerState(
    val partners: List<DeliveryPartner> = emptyList(),
    val filterPartner: FilterPartner = FilterPartner.ByPartnerId(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)
