package com.niyaj.popos.realm.expenses_category

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class ExpensesCategoryRealm(): RealmObject{
    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var expansesCategoryName: String = ""

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null
}
