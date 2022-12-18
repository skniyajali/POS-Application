package com.niyaj.popos.realm.addon_item.domain.use_cases

data class AddOnItemUseCases(
    val getAllAddOnItems: GetAllAddOnItems,
    val getAddOnItemById: GetAddOnItemById,
    val findAddOnItemByName: FindAddOnItemByName,
    val createNewAddOnItem: CreateNewAddOnItem,
    val updateAddOnItem: UpdateAddOnItem,
    val deleteAddOnItem: DeleteAddOnItem,
)
