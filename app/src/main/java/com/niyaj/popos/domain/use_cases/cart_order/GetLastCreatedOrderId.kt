package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository

class GetLastCreatedOrderId(
    private val cartOrderRepository: CartOrderRepository
) {

    operator fun invoke(): Long {
        return cartOrderRepository.getLastCreatedOrderId()
    }
}