
package com.niyaj.popos.features.address.domain.model

import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@JsonClass(generateAdapter = true)
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
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ): this() {
        this.addressId = addressId
        this.shortName = shortName
        this.addressName = addressName
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun Address.filterAddress(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.shortName.contains(searchText, true) ||
                this.addressName.contains(searchText, true)
    }else true
}