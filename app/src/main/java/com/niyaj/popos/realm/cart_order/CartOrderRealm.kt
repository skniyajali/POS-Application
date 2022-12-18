package com.niyaj.popos.realm.cart_order

import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.realm.add_on_items.AddOnItemRealm
import com.niyaj.popos.realm.address.AddressRealm
import com.niyaj.popos.realm.customer.CustomerRealm
import com.niyaj.popos.util.Constants
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CartOrderRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ObjectId().toHexString()

    var orderId: String = ""

    var orderType: String? = CartOrderType.DineIn.orderType

    var customer: CustomerRealm? = null

    var address: AddressRealm? = null

    var addOnItems: RealmList<AddOnItemRealm> = realmListOf()

    var doesChargesIncluded: Boolean = true

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var cartOrderStatus: String = OrderStatus.Processing.orderStatus

    var isGlobalAdmin: Boolean = true

    var _partition: String = Constants.REALM_PARTITION_NAME

    var owner_id: String = ""

    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }
}