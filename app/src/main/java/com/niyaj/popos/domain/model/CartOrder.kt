package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.add_on_items.AddOnItem

data class CartOrder(
    val cartOrderId: String = "",
    val orderId: String = "",
    val cartOrderType: String = "",
    val customer: Customer? = null,
    val address: Address? = null,
    val addOnItems: List<AddOnItem> = emptyList(),
    val doesChargesIncluded: Boolean = true,
    val cartOrderStatus: String = "",
    val created_at: String? = null,
    val updated_at: String? = null,
)