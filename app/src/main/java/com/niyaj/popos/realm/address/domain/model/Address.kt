
package com.niyaj.popos.realm.address.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Address : RealmObject {

    @PrimaryKey
    var addressId: String = ""

    var shortName: String = ""

    var addressName: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null
}