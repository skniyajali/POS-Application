package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.address.domain.model.Address

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