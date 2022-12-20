package com.niyaj.popos.features.cart.domain.use_cases

import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.common.util.Resource

class DeleteCartItem(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(cartId: String): Resource<Boolean> {
        return cartRepository.deleteCartById(cartId)
    }
}