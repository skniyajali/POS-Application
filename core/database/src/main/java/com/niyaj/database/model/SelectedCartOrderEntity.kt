package com.niyaj.database.model

import com.niyaj.common.utils.Constants.SELECTED_CART_ORDER_ID
import com.niyaj.model.SelectedCartOrder
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class SelectedCartOrderEntity(): RealmObject {
    @PrimaryKey
    var selectedCartId: String = SELECTED_CART_ORDER_ID

    var cartOrder: CartOrderEntity? = null

    constructor(
        selectedCartId: String = SELECTED_CART_ORDER_ID,
        cartOrder: CartOrderEntity? = null
    ): this() {
        this.selectedCartId = selectedCartId
        this.cartOrder = cartOrder
    }
}

fun SelectedCartOrderEntity.toExternalModel(): SelectedCartOrder {
    return SelectedCartOrder(
        selectedCartId = selectedCartId,
        cartOrder = cartOrder?.toExternalModel()
    )
}