package com.niyaj.database.model

import com.niyaj.model.Address
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AddressEntity() : RealmObject {

    @PrimaryKey
    var addressId: String = ""

    var shortName: String = ""

    var addressName: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        addressId: String = "",
        shortName: String = "",
        addressName: String = "",
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ) : this() {
        this.addressId = addressId
        this.shortName = shortName
        this.addressName = addressName
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun AddressEntity.toExternalModel(): Address {
    return Address(
        addressId = addressId,
        shortName = shortName,
        addressName = addressName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}