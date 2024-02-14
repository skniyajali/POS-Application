package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Charges
import com.niyaj.model.DineInOrder
import com.niyaj.model.DineOutOrder
import com.niyaj.model.OrderDetail
import com.niyaj.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun getDineInOrders(searchText: String, date: String): Flow<List<DineInOrder>>

    suspend fun getDineOutOrders(searchText: String, date: String): Flow<List<DineOutOrder>>

    suspend fun getOrderDetails(cartOrderId: String): OrderDetail?

    suspend fun updateOrderStatus(cartOrderId: String, orderStatus: OrderStatus): Resource<Boolean>

    suspend fun deleteOrder(cartOrderId: String): Resource<Boolean>
}