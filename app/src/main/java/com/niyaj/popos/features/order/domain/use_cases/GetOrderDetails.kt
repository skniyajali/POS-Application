package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetOrderDetails(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(cartOrderId: String): Resource<Cart?> {
        return withContext(Dispatchers.IO){
            orderRepository.getOrderDetails(cartOrderId)
        }
    }
}