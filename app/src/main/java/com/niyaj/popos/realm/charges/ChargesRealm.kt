package com.niyaj.popos.realm.charges

import com.niyaj.popos.util.Constants
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

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}