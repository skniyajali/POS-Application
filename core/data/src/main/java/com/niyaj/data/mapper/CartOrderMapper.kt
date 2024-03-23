package com.niyaj.data.mapper

import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.model.CartOrder
import com.niyaj.model.SelectedCartOrder
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.BsonObjectId

fun CartOrder.toEntity(): CartOrderEntity {
    return CartOrderEntity(
        cartOrderId = cartOrderId.ifEmpty { BsonObjectId().toHexString() },
        orderId = orderId,
        orderType = orderType,
        customer = customer?.toEntity(),
        address = address?.toEntity(),
        addOnItems = addOnItems.map { it.toEntity() }.toRealmList(),
        doesChargesIncluded = doesChargesIncluded,
        cartOrderStatus = cartOrderStatus,
        createdAt = createdAt.ifEmpty { System.currentTimeMillis().toString() },
        updatedAt = updatedAt
    )
}

fun CartOrder.toEntity(
    customerEntity: CustomerEntity?,
    addressEntity: AddressEntity?
): CartOrderEntity {
    return CartOrderEntity(
        cartOrderId = cartOrderId.ifEmpty { BsonObjectId().toHexString() },
        orderId = orderId,
        orderType = orderType,
        customer = customerEntity,
        address = addressEntity,
        addOnItems = addOnItems.map { it.toEntity() }.toRealmList(),
        doesChargesIncluded = doesChargesIncluded,
        cartOrderStatus = cartOrderStatus,
        createdAt = createdAt.ifEmpty { System.currentTimeMillis().toString() },
        updatedAt = updatedAt
    )
}


fun SelectedCartOrder.toEntity(): SelectedCartOrderEntity {
    return SelectedCartOrderEntity(
        selectedCartId = selectedCartId,
        cartOrder = cartOrder?.toEntity()
    )
}