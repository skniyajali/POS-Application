package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.repository.CartOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSelectedCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(): Flow<CartOrder?> {
        return flow {
            cartOrderRepository.getSelectedCartOrders().collect{ cartOrderId ->
                if (cartOrderId?.cartOrder?._id != null){
                    val cartOrder = cartOrderRepository.getCartOrderById(cartOrderId.cartOrder?._id!!)
                    emit(cartOrder.data)
                }else{
                    emit(null)
                }
            }
        }
    }
}