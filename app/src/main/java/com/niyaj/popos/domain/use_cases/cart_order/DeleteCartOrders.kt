package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCartOrders(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(deleteAll: Boolean = false): Resource<Boolean> {
        return cartOrderRepository.deleteCartOrders(deleteAll)
    }
}