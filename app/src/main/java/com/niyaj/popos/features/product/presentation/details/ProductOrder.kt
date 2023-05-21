package com.niyaj.popos.features.product.presentation.details

data class ProductOrder(
    val cartOrderId: String = "",
    val orderId: String = "",
    val orderedDate: String = "",
    val orderType: String = "",
    val quantity: Int = 0,
    val customerPhone: String? = null,
    val customerAddress: String? = null,
)


data class ProductOrderState(
    val productOrders: List<ProductOrder> = emptyList(),
    val productPrice: Int = 0,
    val isLoading: Boolean = true,
    val hasError: String? = null,
)

data class ProductTotalOrderDetails(
    val totalAmount: String = "0",
    val dineInAmount: String = "0",
    val dineInQty: Int = 0,
    val dineOutAmount: String = "0",
    val dineOutQty: Int = 0,
    val mostOrderItemDate: String = "",
    val mostOrderQtyDate: String = "",
    val datePeriod: Pair<String, String> = Pair("", "")
)