package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource

class GetCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {
    suspend operator fun invoke(cartOrderId: String): Resource<CartOrder?> {
        return cartOrderRepository.getCartOrderById(cartOrderId)
    }
}