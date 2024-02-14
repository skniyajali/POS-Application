package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>>

    fun getAllDineInOrders(): Flow<List<CartItem>>

    fun getAllDineOutOrders(): Flow<List<CartItem>>

    suspend fun addProductToCart(cartOrderId: String, productId: String): Resource<Boolean>

    suspend fun removeProductFromCart(cartOrderId: String, productId: String): Resource<Boolean>

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

    /**
     * Delete old data before current date
     * @return [Resource] of [Boolean] type.
     */
    suspend fun deletePastData(): Resource<Boolean>

}