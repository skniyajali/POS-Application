package com.niyaj.feature.charges.add_edit

sealed class AddEditChargesEvent{

    data class ChargesNameChanged(val chargesName: String) : AddEditChargesEvent()

    data class ChargesPriceChanged(val chargesPrice: String) : AddEditChargesEvent()

    data object ChargesApplicableChanged : AddEditChargesEvent()

    data object CreateOrUpdateCharges : AddEditChargesEvent()
}
