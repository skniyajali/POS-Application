package com.niyaj.popos.realm.delivery_partner

import com.niyaj.popos.util.Constants
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class PartnerRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var partnerName: String = ""

    var partnerEmail: String = ""

    var partnerPhone: String = ""

    var partnerPassword: String = ""

    var partnerStatus: String = ""

    var partnerType: String = ""

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}