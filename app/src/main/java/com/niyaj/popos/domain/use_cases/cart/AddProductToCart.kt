package com.niyaj.popos.domain.use_cases.cart

import com.niyaj.popos.domain.repository.CartRepository
import com.niyaj.popos.domain.util.Resource

class AddProductToCart(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(cartId: String, productId: String): Resource<Boolean> {
        return cartRepository.addProductToCart(cartId, productId)
    }
}