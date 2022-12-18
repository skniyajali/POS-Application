package com.niyaj.popos.realm.order

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import kotlinx.coroutines.flow.Flow

interface OrderRealmDao {

    suspend fun getAllOrders(startDate: String, endDate: String): Flow<Resource<List<CartRealm>>>

    suspend fun getOrderDetails(cartOrderId: String): Resource<List<CartRealm>>

    suspend fun updateOrderStatus(cartOrderId: String, orderStatus: String): Resource<Boolean>

    suspend fun deleteOrder(cartOrderId: String): Resource<Boolean>

}