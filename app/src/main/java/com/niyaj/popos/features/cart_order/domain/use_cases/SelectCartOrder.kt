package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource

class SelectCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(cartOrderId: String): Resource<Boolean> {
        val result = cartOrderRepository.addSelectedCartOrder(cartOrderId)

        return if (result){
            Resource.Success(true)
        }else {
            Resource.Error("Unable to select cart order")
        }
    }
}