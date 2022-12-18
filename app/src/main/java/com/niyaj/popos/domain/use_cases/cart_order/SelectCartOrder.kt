package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource

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