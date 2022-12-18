package com.niyaj.popos.presentation.add_on_items.add_edit

sealed class AddEditAddOnItemEvent {

    data class ItemNameChanged(val itemName: String) : AddEditAddOnItemEvent()

    data class ItemPriceChanged(val itemPrice: String) : AddEditAddOnItemEvent()

    object CreateNewAddOnItem : AddEditAddOnItemEvent()

    data class UpdateAddOnItem(val addOnItemId: String) : AddEditAddOnItemEvent()
}
