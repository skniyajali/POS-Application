package com.niyaj.popos.features.cart.domain.repository

import com.niyaj.popos.features.cart.domain.model.CartItem
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun getAllDineInOrders(): Flow<Resource<List<CartItem>>>

    suspend fun getAllDineOutOrders(): Flow<Resource<List<CartItem>>>

    suspend fun addProductToCart(cartOrderId: String, productId: String): Resource<Boolean>

    suspend fun removeProductFromCart(cartOrderId: String, productId: String): Resource<Boolean>

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

    /**
     * Delete old data before current date
     * @return [Resource] of [Boolean] type.
     */
    suspend fun deletePastData(): Resource<Boolean>

}