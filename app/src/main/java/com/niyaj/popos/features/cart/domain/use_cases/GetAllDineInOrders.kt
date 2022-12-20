package com.niyaj.popos.features.cart.domain.use_cases

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAllDineInOrders(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke(): Flow<Resource<List<Cart>>> {
        return cartRepository.getAllDineInOrders()
    }
}