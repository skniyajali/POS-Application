package com.niyaj.popos.presentation.delivery_partner

import com.niyaj.popos.domain.util.filter_items.FilterPartner

sealed class PartnerEvent{

    data class SelectPartner(val partnerId: String) : PartnerEvent()

    data class DeletePartner(val partnerId: String) : PartnerEvent()

    data class OnFilterPartner(val filterPartner: FilterPartner): PartnerEvent()

    data class OnSearchPartner(val searchText: String): PartnerEvent()

    object ToggleSearchBar : PartnerEvent()

    object RefreshPartner : PartnerEvent()
}
