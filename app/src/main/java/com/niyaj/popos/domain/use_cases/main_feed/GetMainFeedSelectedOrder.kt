package com.niyaj.popos.domain.use_cases.main_feed

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.repository.MainFeedRepository
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