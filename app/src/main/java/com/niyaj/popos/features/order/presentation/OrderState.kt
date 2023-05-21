package com.niyaj.popos.features.order.presentation

import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.domain.model.DineOutOrder

data class DineInOrderState(
    val dineInOrders: List<DineInOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DineOutOrderState(
    val dineOutOrders: List<DineOutOrder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)