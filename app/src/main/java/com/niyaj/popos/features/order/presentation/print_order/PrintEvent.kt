package com.niyaj.popos.features.order.presentation.print_order

sealed class PrintEvent {
    
    data class PrintOrder(val cartOrder: String): PrintEvent()

    data class PrintOrders(val cartOrders: List<String>): PrintEvent()

    data class PrintAllExpenses(val startDate: String, val endDate: String): PrintEvent()
}
