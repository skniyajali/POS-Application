package com.niyaj.popos.presentation.add_on_items.add_edit

data class AddEditAddOnItemState(
    val itemName: String = "",
    val itemNameError: String? = null,
    val itemPrice: String = "",
    val itemPriceError: String? = null
)
