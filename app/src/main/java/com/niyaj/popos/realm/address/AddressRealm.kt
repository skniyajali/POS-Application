package com.niyaj.popos.realm.address

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class AddressRealm() : RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var shortName: String = ""

    var addressName: String = ""

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null
}