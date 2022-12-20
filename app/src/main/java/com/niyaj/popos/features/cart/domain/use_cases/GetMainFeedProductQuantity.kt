package com.niyaj.popos.features.cart.domain.use_cases

import com.niyaj.popos.features.cart.domain.repository.CartRepository

class GetMainFeedProductQuantity(
    private val cartRepository: CartRepository
) {

    operator fun invoke(cartOrderId: String, productId: String): Int {
        return cartRepository.getMainFeedProductQuantity(cartOrderId, productId)
    }
}