package com.niyaj.data.mapper

import com.niyaj.database.model.AddressEntity
import com.niyaj.model.Address
import org.mongodb.kbson.BsonObjectId

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        addressId = addressId.ifEmpty { BsonObjectId().toHexString() },
        shortName = shortName,
        addressName = addressName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}