package com.niyaj.popos.realm.customer

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
}