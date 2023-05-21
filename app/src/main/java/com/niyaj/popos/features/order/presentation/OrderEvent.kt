package com.niyaj.popos.features.order.presentation

/**
 * Order event is used to handle all the events that are triggered from the UI
 */
sealed class OrderEvent{
    data class DeleteOrder(val cartOrderId: String) : OrderEvent()

    data class MarkedAsProcessing(val cartOrderId: String): OrderEvent()

    data class MarkedAsDelivered(val cartOrderId: String): OrderEvent()

    data class OnSearchOrder(val searchText: String): OrderEvent()

    data class SelectDate(val date: String): OrderEvent()

    object ToggleSearchBar : OrderEvent()

    object PrintDeliveryReport : OrderEvent()
    
    object RefreshOrder : OrderEvent()
}
