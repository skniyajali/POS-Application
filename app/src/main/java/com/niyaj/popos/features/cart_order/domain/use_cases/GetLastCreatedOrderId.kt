package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository

class GetLastCreatedOrderId(
    private val cartOrderRepository: CartOrderRepository
) {

    operator fun invoke(): Long {
        return cartOrderRepository.getLastCreatedOrderId()
    }
}