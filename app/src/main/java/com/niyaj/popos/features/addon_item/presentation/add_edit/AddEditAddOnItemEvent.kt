package com.niyaj.popos.features.addon_item.presentation.add_edit

sealed class AddEditAddOnItemEvent {

    data class ItemNameChanged(val itemName: String) : AddEditAddOnItemEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddEditAddOnItemEvent()

    object ItemApplicableChanged: AddEditAddOnItemEvent()

    object CreateNewAddOnItem : AddEditAddOnItemEvent()

    data class UpdateAddOnItem(val addOnItemId: String) : AddEditAddOnItemEvent()
}
