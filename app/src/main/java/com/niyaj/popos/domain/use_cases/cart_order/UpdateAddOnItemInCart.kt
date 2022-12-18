package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

class UpdateAddOnItemInCart(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(addOnItemId: String, cartOrderId: String): Resource<Boolean>{
        return cartOrderRepository.updateAddOnItem(addOnItemId, cartOrderId)
    }
}