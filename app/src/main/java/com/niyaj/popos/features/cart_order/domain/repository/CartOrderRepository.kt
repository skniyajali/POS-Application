package com.niyaj.popos.features.cart_order.domain.repository

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.common.util.Resource
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

    suspend fun getSelectedCartOrders(): Flow<SelectedCartOrder?>

    suspend fun addSelectedCartOrder(cartOrderId: String): Boolean

    suspend fun deleteSelectedCartOrder(): Boolean

    suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean>
}