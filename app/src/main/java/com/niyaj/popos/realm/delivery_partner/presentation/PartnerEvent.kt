package com.niyaj.popos.realm.delivery_partner.presentation

import com.niyaj.popos.realm.delivery_partner.domain.util.FilterPartner

sealed class PartnerEvent{

    data class SelectPartner(val partnerId: String) : PartnerEvent()

    data class DeletePartner(val partnerId: String) : PartnerEvent()

    data class OnFilterPartner(val filterPartner: FilterPartner): PartnerEvent()

    data class OnSearchPartner(val searchText: String): PartnerEvent()

    object ToggleSearchBar : PartnerEvent()

    object RefreshPartner : PartnerEvent()
}
