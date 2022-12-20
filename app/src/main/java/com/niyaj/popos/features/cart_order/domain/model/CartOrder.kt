package com.niyaj.popos.features.cart_order.domain.model

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.customer.domain.model.Customer
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CartOrder(): RealmObject {

    @PrimaryKey
    var cartOrderId: String = ""

    var orderId: String = ""

    var orderType: String = CartOrderType.DineIn.orderType

    var customer: Customer? = null

    var address: Address? = null

    var addOnItems: RealmList<AddOnItem> = realmListOf()

    var doesChargesIncluded: Boolean = true

    var cartOrderStatus: String = OrderStatus.Processing.orderStatus

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        cartOrderId: String = "",
        orderId: String = "",
        orderType: String = CartOrderType.DineIn.orderType,
        customer: Customer? = null,
        address: Address? = null,
        addOnItems: RealmList<AddOnItem> = realmListOf(),
        doesChargesIncluded: Boolean = true,
        cartOrderStatus: String = OrderStatus.Processing.orderStatus,
        createdAt : String = "",
        updatedAt : String? = null
    ): this() {
        this.cartOrderId = cartOrderId
        this.orderId = orderId
        this.orderType = orderType
        this.customer = customer
        this.address = address
        this.addOnItems = addOnItems
        this.doesChargesIncluded = doesChargesIncluded
        this.cartOrderStatus = cartOrderStatus
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}