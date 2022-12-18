package com.niyaj.popos.realm.cart_order

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartOrderRealmDao {

    fun getLastCreatedOrderId(): Long

    suspend fun getAllCartOrders(): Flow<Resource<List<CartOrderRealm>>>

    suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrderRealm?>

    suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean>

    suspend fun updateCartOrder(newOrder: CartOrder, cartOrderId: String): Resource<Boolean>

    suspend fun updateAddOnItem(addOnItemId: String, cartOrderId: String): Resource<Boolean>

    suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean>

    suspend fun placeOrder(cartOrderId: String): Resource<Boolean>

    suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean>

    suspend fun getSelectedCartOrders(): Flow<SelectedCartOrderRealm?>

    suspend fun addSelectedCartOrder(cartOrderId: String): Boolean

    suspend fun deleteSelectedCartOrder(): Boolean

    suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean>
}