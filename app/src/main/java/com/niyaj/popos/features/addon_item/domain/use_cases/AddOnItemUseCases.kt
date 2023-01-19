package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.use_cases.validation.ValidateItemName
import com.niyaj.popos.features.addon_item.domain.use_cases.validation.ValidateItemPrice

data class AddOnItemUseCases(
    val validateItemName: ValidateItemName,
    val validateItemPrice: ValidateItemPrice,
    val getAllAddOnItems: GetAllAddOnItems,
    val getAddOnItemById: GetAddOnItemById,
    val createNewAddOnItem: CreateNewAddOnItem,
    val updateAddOnItem: UpdateAddOnItem,
    val deleteAddOnItem: DeleteAddOnItem,
)
