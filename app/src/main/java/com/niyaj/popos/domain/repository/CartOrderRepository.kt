package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import kotlinx.coroutines.flow.Flow

interface CartOrderRepository {

    fun getLastCreatedOrderId(): Long

    suspend fun getAllCartOrders(): Flow<Resource<List<CartOrder>>>

    suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?>

    suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean>

    suspend fun updateCartOrder(newOrder: CartOrder, cartOrderId: String): Resource<Boolean>

    suspend fun updateAddOnItem(addOnItemId: String, cartOrderId: String): Resource<Boolean>

    suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean>

    suspend fun placeOrder(cartOrderId: String): Resource<Boolean>

    suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean>

    suspend fun getSelectedCartOrders(): Flow<SelectedCartOrderRealm?>

    suspend fun addSelectedCartOrder(cartOrderId: String): Boolean

    suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean>



}