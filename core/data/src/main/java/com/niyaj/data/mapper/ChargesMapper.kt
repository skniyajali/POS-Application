package com.niyaj.data.mapper

import com.niyaj.database.model.ChargesEntity
import com.niyaj.model.Charges
import org.mongodb.kbson.BsonObjectId

fun Charges.toEntity(): ChargesEntity {
    return ChargesEntity(
        chargesId = chargesId.ifEmpty { BsonObjectId().toHexString() },
        chargesName = chargesName,
        chargesPrice = chargesPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}