package com.niyaj.popos.features.order.domain.use_cases

data class OrderUseCases(
    val getAllOrders: GetAllOrders,
    val changeOrderStatus: ChangeOrderStatus,
    val getOrderDetails: GetOrderDetails,
    val deleteOrder: DeleteOrder,
)
