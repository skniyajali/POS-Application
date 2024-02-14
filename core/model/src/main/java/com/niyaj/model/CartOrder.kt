package com.niyaj.model

data class CartOrder(
    val cartOrderId: String = "",

    val orderId: String = "",

    val orderType: OrderType = OrderType.DineIn,

    val customer: Customer? = null,

    val address: Address? = null,

    val addOnItems: List<AddOnItem> = emptyList(),

    val doesChargesIncluded: Boolean = true,

    val cartOrderStatus: OrderStatus = OrderStatus.PROCESSING,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<CartOrder>.searchCartOrder(searchText: String): List<CartOrder> {
    return if (searchText.isNotEmpty()){
        this.filter {
            it.cartOrderStatus.name.contains(searchText, true) ||
            it.cartOrderId.contains(searchText, true) ||
            it.customer?.customerPhone?.contains(searchText, true) == true ||
            it.customer?.customerName?.contains(searchText, true) == true ||
            it.address?.addressName?.contains(searchText, true) == true ||
            it.address?.shortName?.contains(searchText, true) == true ||
            it.orderType.name.contains(searchText, true) ||
            it.orderId.contains(searchText, true) ||
            it.createdAt.contains(searchText, true) ||
            it.updatedAt?.contains(searchText, true) == true
        }
    }else {
        this
    }
}