package com.niyaj.popos.realm.addon_item.presentation.add_edit

data class AddEditAddOnItemState(
    val itemName: String = "",
    val itemNameError: String? = null,
    val itemPrice: String = "",
    val itemPriceError: String? = null
)
