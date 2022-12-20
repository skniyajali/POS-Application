package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMainFeedSelectedOrder(
    private val mainFeedRepository: MainFeedRepository,
    private val cartOrderRepository: CartOrderRepository
) {

    suspend operator fun invoke(): Flow<CartOrder?>{
        return flow {
            mainFeedRepository.getSelectedCartOrders().collect{ cartOrderId ->
                if (cartOrderId != null){
                    val cartOrder = cartOrderRepository.getCartOrderById(cartOrderId)
                    emit(cartOrder.data)
                }else{
                    emit(null)
                }
            }
        }
    }
}