package com.niyaj.data.repository

import com.niyaj.model.CartOrder
import kotlinx.coroutines.flow.Flow

interface SelectedRepository {

    suspend fun getAllProcessingCartOrder(): Flow<List<CartOrder>>

    suspend fun getSelectedCartOrders(): Flow<String?>

    suspend fun markOrderAsSelected(cartOrderId: String): Boolean
}