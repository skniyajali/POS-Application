package com.niyaj.feature.print

sealed class PrintEvent {
    
    data class PrintOrder(val cartOrder: String): PrintEvent()

    data class PrintOrders(val cartOrders: List<String>): PrintEvent()

    data class PrintAllExpenses(val startDate: String, val endDate: String): PrintEvent()

    data class PrintDeliveryReport(val date: String) : PrintEvent()

}
