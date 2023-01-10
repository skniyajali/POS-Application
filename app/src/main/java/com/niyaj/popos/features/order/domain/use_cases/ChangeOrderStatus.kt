package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository

class ChangeOrderStatus(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String, orderStatus: String): Resource<Boolean> {
        return orderRepository.updateOrderStatus(cartOrderId, orderStatus)

    }
}