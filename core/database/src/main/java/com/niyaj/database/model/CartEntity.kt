package com.niyaj.database.model

import com.niyaj.model.Cart
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class CartEntity() : RealmObject {

    @PrimaryKey
    var cartId: String = BsonObjectId().toHexString()

    var cartOrder: CartOrderEntity? = null

    var product: ProductEntity? = null

    var quantity: Int = 0

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

    constructor(
        cartId: String = BsonObjectId().toHexString(),
        cartOrder: CartOrderEntity? = null,
        product: ProductEntity? = null,
        quantity: Int = 0,
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null,
    ) : this() {
        this.cartId = cartId
        this.cartOrder = cartOrder
        this.product = product
        this.quantity = quantity
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun CartEntity.toExternalModel(): Cart {
    return Cart(
        cartId = cartId,
        cartOrder = cartOrder?.toExternalModel(),
        product = product?.toExternalModel(),
        quantity = quantity,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}