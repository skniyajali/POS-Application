package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

class GetCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<CartOrder?> {
        return cartOrderRepository.getCartOrderById(cartOrderId)
    }

}