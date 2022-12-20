
package com.niyaj.popos.features.address.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Address() : RealmObject {

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
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.addressId = addressId
        this.shortName = shortName
        this.addressName = addressName
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}