package com.niyaj.popos.features.cart_order.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class SelectedCartOrder: RealmObject {
    @PrimaryKey
    var selectedCartId: String = BsonObjectId().toHexString()

    var cartOrder: CartOrder? = null
}