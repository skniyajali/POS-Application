package com.niyaj.popos.realm.cart_order

import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.customer.domain.model.Customer
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class CartOrderRealm(): RealmObject {

    @PrimaryKey
    var _id: String = BsonObjectId().toHexString()

    var orderId: String = ""

    var orderType: String? = CartOrderType.DineIn.orderType

    var customer: Customer? = null

    var address: Address? = null

    var addOnItems: RealmList<AddOnItem> = realmListOf()

    var doesChargesIncluded: Boolean = true

    var created_at: String = System.currentTimeMillis().toString()

    var updated_at: String? = null

    var cartOrderStatus: String = OrderStatus.Processing.orderStatus
}