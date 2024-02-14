package com.niyaj.data.mapper

import com.niyaj.database.model.CartEntity
import com.niyaj.model.Cart

fun Cart.toExternalModel(): CartEntity {
    return CartEntity(
        cartId = cartId,
        cartOrder = cartOrder?.toEntity(),
        product = product?.toEntity(),
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}