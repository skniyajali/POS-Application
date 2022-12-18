package com.niyaj.popos.realm.add_on_items

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class AddOnItemRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var itemName: String = ""

    var itemPrice: Int = 0

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null
}