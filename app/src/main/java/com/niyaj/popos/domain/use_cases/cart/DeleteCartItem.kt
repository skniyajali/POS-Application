package com.niyaj.popos.domain.use_cases.cart

import com.niyaj.popos.domain.repository.CartRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCartItem(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(cartId: String): Resource<Boolean> {
        return cartRepository.deleteCartProductsById(cartId)
    }
}