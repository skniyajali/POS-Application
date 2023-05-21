package com.niyaj.popos.features.main_feed.domain.repository

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.main_feed.domain.model.ProductWithFlowQuantity
import kotlinx.coroutines.flow.Flow

interface MainFeedRepository {

    suspend fun getSelectedCartOrders(): Flow<CartOrder?>

    suspend fun geMainFeedProducts(): Flow<Resource<List<ProductWithFlowQuantity>>>
}