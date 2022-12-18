package com.niyaj.popos.domain.use_cases.order

import com.niyaj.popos.domain.repository.OrderRepository
import com.niyaj.popos.domain.util.Resource

class ChangeOrderStatus(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String, orderStatus: String): Resource<Boolean> {
        return orderRepository.updateOrderStatus(cartOrderId, orderStatus)
    }
}