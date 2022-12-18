package com.niyaj.popos.domain.use_cases.add_on_item

data class AddOnItemUseCases(
    val getAllAddOnItems: GetAllAddOnItems,
    val getAddOnItemById: GetAddOnItemById,
    val findAddOnItemByName: FindAddOnItemByName,
    val createNewAddOnItem: CreateNewAddOnItem,
    val updateAddOnItem: UpdateAddOnItem,
    val deleteAddOnItem: DeleteAddOnItem,
)
