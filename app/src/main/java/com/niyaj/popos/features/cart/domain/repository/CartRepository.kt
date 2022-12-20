package com.niyaj.popos.features.cart.domain.repository

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.cart.domain.model.CartProduct
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun getAllCartProducts(): Flow<Resource<List<Cart>>>

    suspend fun getAllDineInOrders(): Flow<Resource<List<Cart>>>

    suspend fun getAllDineOutOrders(): Flow<Resource<List<Cart>>>

    suspend fun getCartByCartId(cartId: String): Resource<CartProduct?>

    suspend fun getCartByCartOrderId(cartOrderId: String): Flow<Resource<List<CartProduct>>>

    suspend fun addProductToCart(cartOrderId: String, productId: String): Resource<Boolean>

    suspend fun removeProductFromCart(cartOrderId: String, productId: String): Resource<Boolean>

    suspend fun deleteCartById(cartId: String): Resource<Boolean>

    suspend fun deleteCartByCartOrderId(cartOrderId: String): Resource<Boolean>

    fun getMainFeedProductQuantity(cartOrderId: String, productId: String): Int

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

    /**
     * Delete old data from current start time
     * @return [Resource] of [Boolean] type.
     */
    suspend fun deletePastData(): Resource<Boolean>

}