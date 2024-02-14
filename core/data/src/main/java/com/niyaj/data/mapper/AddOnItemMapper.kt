package com.niyaj.data.mapper

import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.model.AddOnItem

fun AddOnItem.toEntity(): AddOnItemEntity {
    return AddOnItemEntity(
        addOnItemId = addOnItemId,
        itemName = itemName,
        itemPrice = itemPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}