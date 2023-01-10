package com.niyaj.popos.features.addon_item.presentation.add_edit

data class AddEditAddOnItemState(
    val itemName: String = "",
    val itemNameError: String? = null,
    val itemPrice: String = "",
    val itemPriceError: String? = null,
    val serverError: String? = null
)
