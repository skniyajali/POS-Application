package com.niyaj.data.repository

import com.niyaj.model.CartOrder
import kotlinx.coroutines.flow.Flow

interface SelectedRepository {

    fun getAllProcessingCartOrder(): Flow<List<CartOrder>>

    fun getSelectedCartOrders(): Flow<String?>

    suspend fun markOrderAsSelected(cartOrderId: String): Boolean
}