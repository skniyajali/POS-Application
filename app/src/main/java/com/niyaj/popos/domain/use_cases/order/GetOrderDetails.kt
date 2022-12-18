package com.niyaj.popos.domain.use_cases.order

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.repository.OrderRepository
import com.niyaj.popos.domain.util.Resource

class GetOrderDetails(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Cart?> {
        return orderRepository.getOrderDetails(cartOrderId)
    }
}