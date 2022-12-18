package com.niyaj.popos.domain.use_cases.cart

import com.niyaj.popos.domain.repository.CartRepository

class GetMainFeedProductQuantity(
    private val cartRepository: CartRepository
) {

    operator fun invoke(cartId: String, productId: String): Int {
        return cartRepository.getMainFeedProductQuantity(cartId, productId)
    }
}