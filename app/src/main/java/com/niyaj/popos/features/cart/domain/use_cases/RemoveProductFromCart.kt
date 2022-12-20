package com.niyaj.popos.features.cart.domain.use_cases

import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.common.util.Resource

class RemoveProductFromCart(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(cartOrderId: String, productId: String): Resource<Boolean> {
        return cartRepository.removeProductFromCart(cartOrderId, productId)
    }
}