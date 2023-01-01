package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateAddOnItemInCart(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(addOnItemId: String, cartOrderId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO){
            cartOrderRepository.updateAddOnItem(addOnItemId, cartOrderId)
        }
    }
}