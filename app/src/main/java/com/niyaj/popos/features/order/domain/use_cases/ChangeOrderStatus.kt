package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangeOrderStatus(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(cartOrderId: String, orderStatus: String): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            orderRepository.updateOrderStatus(cartOrderId, orderStatus)
        }
    }
}