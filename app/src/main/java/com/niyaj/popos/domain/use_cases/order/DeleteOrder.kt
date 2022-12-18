package com.niyaj.popos.domain.use_cases.order

import com.niyaj.popos.domain.repository.OrderRepository
import com.niyaj.popos.domain.util.Resource

class DeleteOrder(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Boolean> {
        return orderRepository.deleteOrder(cartOrderId)
    }
}