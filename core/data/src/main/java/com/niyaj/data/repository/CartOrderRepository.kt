package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import kotlinx.coroutines.flow.Flow

interface CartOrderRepository {

    suspend fun getLastCreatedOrderId(cartOrderId: String): String

    suspend fun getAllAddress(searchText: String): Flow<List<Address>>

    /**
     * Get all customers from database.
     * @return [Flow] of [Resource] of [List] of [Customer] objects.
     * @see Customer
     */
    suspend fun getAllCustomers(searchText: String): Flow<List<Customer>>

    suspend fun getAllCartOrders(viewAll: Boolean = false): Flow<List<CartOrder>>

    suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?>

    suspend fun createOrUpdateCartOrder(newOrder: CartOrder, cartOrderId: String): Resource<Boolean>

    suspend fun updateAddOnItem(addOnItemId: String, cartOrderId: String): Resource<Boolean>

    suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean>

    suspend fun deleteCartOrders(cartOrderIds: List<String>): Resource<Boolean>

    suspend fun placeOrder(cartOrderId: String): Resource<Boolean>

    suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean>

    fun getSelectedCartOrders(): Flow<String?>

    suspend fun addSelectedCartOrder(cartOrderId: String): Boolean

    suspend fun deleteSelectedCartOrder(): Boolean

    suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean>
}