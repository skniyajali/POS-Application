package com.niyaj.data.mapper

import com.niyaj.database.model.AddressEntity
import com.niyaj.model.Address

fun Address.toEntity(): AddressEntity {
    return AddressEntity(
        addressId = addressId,
        shortName = shortName,
        addressName = addressName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}