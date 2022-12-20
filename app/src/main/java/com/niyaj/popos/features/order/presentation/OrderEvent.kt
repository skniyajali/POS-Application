package com.niyaj.popos.features.order.presentation

import com.niyaj.popos.features.order.domain.util.FilterOrder

sealed class OrderEvent{
    data class DeleteOrder(val cartOrderId: String) : OrderEvent()

    data class MarkedAsProcessing(val cartOrderId: String): OrderEvent()

    data class MarkedAsDelivered(val cartOrderId: String): OrderEvent()

    data class OnFilterOrder(val filterOrder: FilterOrder): OrderEvent()

    data class OnSearchOrder(val searchText: String): OrderEvent()

    data class SelectDate(val date: String): OrderEvent()

    object ToggleSearchBar : OrderEvent()

    object PrintDeliveryReport : OrderEvent()
    
    object RefreshOrder : OrderEvent()
}
