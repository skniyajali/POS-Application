package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            cartOrderRepository.deleteCartOrder(cartOrderId)
        }
    }
}