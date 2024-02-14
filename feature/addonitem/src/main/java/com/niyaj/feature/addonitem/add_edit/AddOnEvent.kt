package com.niyaj.feature.addonitem.add_edit

sealed class AddOnEvent {

    data class ItemNameChanged(val itemName: String) : AddOnEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddOnEvent()

    data object ItemApplicableChanged: AddOnEvent()

    data object CreateUpdateAddOnItem : AddOnEvent()
}
