package com.niyaj.data.repository

import com.niyaj.model.CartOrder
import com.niyaj.model.ProductWithFlowQuantity
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getSelectedCartOrders(): Flow<CartOrder?>

    suspend fun geMainFeedProducts(
        selectedCategory: String,
        searchText: String
    ): Flow<List<ProductWithFlowQuantity>>
}