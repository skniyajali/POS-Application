package com.niyaj.popos.realm.delivery_partner

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
}