package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource

class PlaceAllOrder(
    private val cartOrderRepository: CartOrderRepository
) {
    suspend operator fun invoke(cartOrderIds: List<String>): Resource<Boolean> {
        return cartOrderRepository.placeAllOrder(cartOrderIds)
    }
}