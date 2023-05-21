package com.niyaj.popos.features.order.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.domain.model.DineOutOrder
import com.niyaj.popos.features.order.domain.model.OrderDetail
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getDineInOrders(startDate: String, endDate: String): Flow<Resource<List<DineInOrder>>>

    suspend fun getDineOutOrders(startDate: String, endDate: String): Flow<Resource<List<DineOutOrder>>>

    suspend fun getOrderDetails(cartOrderId: String): Resource<OrderDetail?>

    suspend fun updateOrderStatus(cartOrderId: String, orderStatus: String): Resource<Boolean>

    suspend fun deleteOrder(cartOrderId: String): Resource<Boolean>

}