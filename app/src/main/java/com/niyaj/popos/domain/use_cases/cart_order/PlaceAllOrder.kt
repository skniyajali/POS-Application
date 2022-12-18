package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

class PlaceAllOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(cartOrderIds: List<String>): Resource<Boolean> {
        return cartOrderRepository.placeAllOrder(cartOrderIds)
    }
}