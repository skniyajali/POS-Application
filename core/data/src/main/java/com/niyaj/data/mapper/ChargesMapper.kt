package com.niyaj.data.mapper

import com.niyaj.database.model.ChargesEntity
import com.niyaj.model.Charges

fun Charges.toEntity(): ChargesEntity {
    return ChargesEntity(
        chargesId = chargesId,
        chargesName = chargesName,
        chargesPrice = chargesPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}