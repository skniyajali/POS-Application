package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Boolean>{
        return cartOrderRepository.deleteCartOrder(cartOrderId)
    }
}