package com.niyaj.feature.order

/**
 * Order event is used to handle all the events that are triggered from the UI
 */
sealed class OrderEvent{
    data class DeleteOrder(val cartOrderId: String) : OrderEvent()

    data class MarkedAsProcessing(val cartOrderId: String): OrderEvent()

    data class SelectDate(val date: String): OrderEvent()
}
