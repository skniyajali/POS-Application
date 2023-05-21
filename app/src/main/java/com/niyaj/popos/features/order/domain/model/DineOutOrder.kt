package com.niyaj.popos.features.order.domain.model

data class DineOutOrder(
    val cartOrderId: String = "",
    val orderId: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val totalAmount: String = "",
    val updatedAt: String = "",
)


fun DineOutOrder.searchDineOutOrder(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.orderId.contains(searchText, true) ||
        this.customerAddress.contains(searchText, true) ||
        this.customerPhone.contains(searchText, true) ||
        this.totalAmount.contains(searchText, true)
    }else true
}