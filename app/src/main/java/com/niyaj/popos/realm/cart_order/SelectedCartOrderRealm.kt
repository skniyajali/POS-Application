package com.niyaj.popos.realm.cart_order

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class SelectedCartOrderRealm: RealmObject {
    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var cartOrder: CartOrderRealm? = null
}