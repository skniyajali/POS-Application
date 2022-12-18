package com.niyaj.popos.domain.use_cases.order

data class OrderUseCases(
    val getAllOrders: GetAllOrders,
    val changeOrderStatus: ChangeOrderStatus,
    val getOrderDetails: GetOrderDetails,
    val deleteOrder: DeleteOrder,
)
