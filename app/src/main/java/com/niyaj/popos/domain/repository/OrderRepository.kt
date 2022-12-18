package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getAllOrders(startDate: String, endDate: String): Flow<Resource<List<Cart>>>

    suspend fun getOrderDetails(cartOrderId: String): Resource<Cart?>

    suspend fun updateOrderStatus(cartOrderId: String, orderStatus: String): Resource<Boolean>

    suspend fun deleteOrder(cartOrderId: String): Resource<Boolean>

}