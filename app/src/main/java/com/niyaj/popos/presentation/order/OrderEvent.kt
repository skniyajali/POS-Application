package com.niyaj.popos.presentation.order

import com.niyaj.popos.domain.util.filter_items.FilterOrder

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
