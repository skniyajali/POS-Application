package com.niyaj.popos.features.cart.domain.model

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.product.domain.model.Product
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId

class CartRealm(): RealmObject {
    
    @PrimaryKey
    var cartId: String = BsonObjectId().toHexString()

    var cartOrder: CartOrder? = null

    var product: Product? = null

    var quantity: Int = 0

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

    constructor(
        cartId: String = BsonObjectId().toHexString(),
        cartOrder: CartOrder? = null,
        product: Product? = null,
        quantity: Int = 0,
        createdAt: String = "",
        updatedAt: String = "",
    ): this() {
        this.cartId = cartId
        this.cartOrder = cartOrder
        this.product = product
        this.quantity = quantity
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}