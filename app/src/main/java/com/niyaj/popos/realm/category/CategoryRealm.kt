package com.niyaj.popos.realm.category

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
}