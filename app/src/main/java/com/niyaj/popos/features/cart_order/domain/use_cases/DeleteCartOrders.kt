package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource

class DeleteCartOrders(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(deleteAll: Boolean = false): Resource<Boolean> {
        return cartOrderRepository.deleteCartOrders(deleteAll)
    }
}