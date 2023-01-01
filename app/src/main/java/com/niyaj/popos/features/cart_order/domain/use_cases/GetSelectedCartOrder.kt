package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class GetSelectedCartOrder(
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(): Flow<CartOrder?> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                cartOrderRepository.getSelectedCartOrders().collect{ cartOrderId ->
                    if (cartOrderId?.cartOrder?.cartOrderId != null){
                        val cartOrder = cartOrderRepository.getCartOrderById(cartOrderId.cartOrder?.cartOrderId!!)
                        send(cartOrder.data)
                    }else{
                        send(null)
                    }
                }
            }
        }
    }
}