package com.niyaj.popos.realm.charges

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ChargesRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var chargesName: String = ""

    var chargesPrice: Int = 0

    var isApplicable: Boolean = false

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null
}