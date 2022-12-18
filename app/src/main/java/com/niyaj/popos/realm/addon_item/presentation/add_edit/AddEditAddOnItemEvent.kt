package com.niyaj.popos.realm.addon_item.presentation.add_edit

sealed class AddEditAddOnItemEvent {

    data class ItemNameChanged(val itemName: String) : AddEditAddOnItemEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddEditAddOnItemEvent()

    object CreateNewAddOnItem : AddEditAddOnItemEvent()

    data class UpdateAddOnItem(val addOnItemId: String) : AddEditAddOnItemEvent()
}
