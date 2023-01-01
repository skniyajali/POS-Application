package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetMainFeedSelectedOrder(
    private val mainFeedRepository: MainFeedRepository,
) {

    suspend operator fun invoke(): Flow<CartOrder?>{
        return withContext(Dispatchers.IO){
            mainFeedRepository.getSelectedCartOrders()
        }
    }
}