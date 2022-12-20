package com.niyaj.popos.features.order.presentation

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.order.domain.util.FilterOrder

data class OrderState(
    val orders: List<Cart> = emptyList(),
    val filterOrder: FilterOrder = FilterOrder.ByUpdatedDate(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)

