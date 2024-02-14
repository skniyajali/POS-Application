package com.niyaj.feature.cart_order.add_edit

import com.niyaj.model.OrderType

data class AddEditCartOrderState(
    val orderType: OrderType = OrderType.DineIn,
    val doesChargesIncluded: Boolean =false,
)
