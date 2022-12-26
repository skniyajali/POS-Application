package com.niyaj.popos.features.cart_order.domain.model

import com.niyaj.popos.util.Constants.SELECTED_CART_ORDER_ID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SelectedCartOrder(): RealmObject {
    @PrimaryKey
    var selectedCartId: String = SELECTED_CART_ORDER_ID

    var cartOrder: CartOrder? = null

    constructor(
        selectedCartId: String = SELECTED_CART_ORDER_ID,
        cartOrder: CartOrder? = null
    ): this() {
        this.selectedCartId = selectedCartId
        this.cartOrder = cartOrder
    }
}