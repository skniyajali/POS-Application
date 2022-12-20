package com.niyaj.popos.features.cart_order.presentation.add_edit

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.customer.domain.model.Customer

data class AddEditCartOrderState(
    val orderId: String = "",
    val orderIdError: String? = null,

    val orderType: String = CartOrderType.DineIn.orderType,

    val customer: Customer? = null,
    val customerError: String? = null,

    val address: Address? = null,
    val addressError: String? = null,
)
