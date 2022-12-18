package com.niyaj.popos.realm.category

import com.niyaj.popos.util.Constants.REALM_PARTITION_NAME
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CategoryRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var categoryName: String = ""

    var categoryAvailability: Boolean = true

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}