package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository

class GetOrderDetails(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(cartOrderId: String): Resource<Cart?> {
        return orderRepository.getOrderDetails(cartOrderId)
    }
}