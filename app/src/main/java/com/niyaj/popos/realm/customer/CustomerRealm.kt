package com.niyaj.popos.realm.customer

import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CustomerRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var customerPhone: String = ""

    var customerName: String? = null

    var customerEmail: String? = null

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}