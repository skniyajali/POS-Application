package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository

class DeleteOrder(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Boolean> {
        return orderRepository.deleteOrder(cartOrderId)
    }
}