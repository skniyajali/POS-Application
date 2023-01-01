package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(order: CartOrder, cartOrderId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            cartOrderRepository.updateCartOrder(order, cartOrderId)
        }
    }
}