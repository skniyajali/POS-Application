package com.niyaj.popos.domain.use_cases.cart

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.repository.CartRepository
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAllCartItems(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(): Flow<Resource<List<Cart>>> {
        return cartRepository.getAllCartProducts()
    }
}