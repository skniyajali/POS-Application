package com.niyaj.data.mapper

import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.model.CartOrder
import com.niyaj.model.SelectedCartOrder
import io.realm.kotlin.ext.toRealmList

fun CartOrder.toEntity(): CartOrderEntity {
    return CartOrderEntity(
        cartOrderId = cartOrderId,
        orderId = orderId,
        orderType = orderType,
        customer = customer?.toEntity(),
        address = address?.toEntity(),
        addOnItems = addOnItems.map { it.toEntity() }.toRealmList(),
        doesChargesIncluded = doesChargesIncluded,
        cartOrderStatus = cartOrderStatus,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}


fun SelectedCartOrder.toEntity(): SelectedCartOrderEntity {
    return SelectedCartOrderEntity(
        selectedCartId = selectedCartId,
        cartOrder = cartOrder?.toEntity()
    )
}