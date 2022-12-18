package com.niyaj.popos.presentation.cart_order.add_edit

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.util.CartOrderType

data class AddEditCartOrderState(
    val orderId: String = "",
    val orderIdError: String? = null,

    val orderType: String = CartOrderType.DineIn.orderType,

    val customer: Customer? = null,
    val customerError: String? = null,

    val address: Address? = null,
    val addressError: String? = null,
)
