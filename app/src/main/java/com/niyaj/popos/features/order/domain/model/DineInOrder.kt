package com.niyaj.popos.features.order.domain.model

data class DineInOrder(
    val cartOrderId: String = "",
    val orderId: String = "",
    val totalAmount: String = "",
    val updatedAt: String = ""
)

fun DineInOrder.searchDineInOrder(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.orderId.contains(searchText, true) || this.totalAmount.contains(searchText, true)
    }else true
}