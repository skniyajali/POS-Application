package com.niyaj.database.model

import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CartOrderEntity() : RealmObject {

    @PrimaryKey
    var cartOrderId: String = ""

    var orderId: String = ""

    var orderType: String = OrderType.DineIn.name

    var customer: CustomerEntity? = null

    var address: AddressEntity? = null

    var addOnItems: RealmList<AddOnItemEntity> = realmListOf()

    var doesChargesIncluded: Boolean = true

    var cartOrderStatus: String = OrderStatus.PROCESSING.name

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        cartOrderId: String = "",
        orderId: String = "",
        orderType: OrderType = OrderType.DineIn,
        customer: CustomerEntity? = null,
        address: AddressEntity? = null,
        addOnItems: RealmList<AddOnItemEntity> = realmListOf(),
        doesChargesIncluded: Boolean = true,
        cartOrderStatus: OrderStatus = OrderStatus.PROCESSING,
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ) : this() {
        this.cartOrderId = cartOrderId
        this.orderId = orderId
        this.orderType = orderType.name
        this.customer = customer
        this.address = address
        this.addOnItems = addOnItems
        this.doesChargesIncluded = doesChargesIncluded
        this.cartOrderStatus = cartOrderStatus.name
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}

fun CartOrderEntity.toExternalModel(): CartOrder {
    return CartOrder(
        cartOrderId = cartOrderId,
        orderId = orderId,
        orderType = OrderType.valueOf(orderType),
        customer = customer?.toExternalModel(),
        address = address?.toExternalModel(),
        addOnItems = addOnItems.map { it.toExternalModel() },
        doesChargesIncluded = doesChargesIncluded,
        cartOrderStatus = OrderStatus.valueOf(cartOrderStatus),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}