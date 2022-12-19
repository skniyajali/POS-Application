package com.niyaj.popos.realm.charges.presentation

import com.niyaj.popos.realm.charges.domain.util.FilterCharges

sealed class ChargesEvent{

    data class ChargesNameChanged(val chargesName: String) : ChargesEvent()

    data class ChargesPriceChanged(val chargesPrice: String) : ChargesEvent()

    object ChargesApplicableChanged : ChargesEvent()

    data class SelectCharges(val chargesId: String) : ChargesEvent()

    object CreateNewCharges : ChargesEvent()

    data class UpdateCharges(val chargesId: String) : ChargesEvent()

    data class DeleteCharges(val chargesId: String) : ChargesEvent()

    data class OnFilterCharges(val filterCharges: FilterCharges): ChargesEvent()

    data class OnSearchCharges(val searchText: String): ChargesEvent()

    object ToggleSearchBar : ChargesEvent()

    object RefreshCharges : ChargesEvent()
}
