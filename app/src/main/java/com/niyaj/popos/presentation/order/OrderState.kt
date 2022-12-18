package com.niyaj.popos.presentation.order

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterOrder

data class OrderState(
    val orders: List<Cart> = emptyList(),
    val filterOrder: FilterOrder = FilterOrder.ByUpdatedDate(SortType.Descending),
    val isLoading: Boolean = false,
    val error: String? = null
)

