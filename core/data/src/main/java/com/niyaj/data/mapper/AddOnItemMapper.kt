package com.niyaj.data.mapper

import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.model.AddOnItem
import org.mongodb.kbson.BsonObjectId

fun AddOnItem.toEntity(): AddOnItemEntity {
    return AddOnItemEntity(
        addOnItemId = addOnItemId.ifEmpty { BsonObjectId().toHexString() },
        itemName = itemName,
        itemPrice = itemPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}