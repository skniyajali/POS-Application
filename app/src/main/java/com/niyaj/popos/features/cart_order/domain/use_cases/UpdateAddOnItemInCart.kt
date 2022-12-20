package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource

class UpdateAddOnItemInCart(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(addOnItemId: String, cartOrderId: String): Resource<Boolean> {
        return cartOrderRepository.updateAddOnItem(addOnItemId, cartOrderId)
    }
}