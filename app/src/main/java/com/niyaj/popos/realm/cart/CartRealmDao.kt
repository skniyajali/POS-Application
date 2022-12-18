package com.niyaj.popos.realm.cart

import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CartRealmDao {

    suspend fun getAllCartProducts(): Flow<Resource<List<CartRealm>>>

    suspend fun getAllDineInOrders(): Flow<Resource<List<CartRealm>>>

    suspend fun getAllDineOutOrders(): Flow<Resource<List<CartRealm>>>

    suspend fun getCartProductsById(cartProductId: String): Resource<CartRealm?>

    suspend fun getCartProductsByOrderId(orderId: String): Flow<Resource<List<CartRealm>>>

    suspend fun addProductToCart(cartId: String, productId: String): Resource<Boolean>

    suspend fun removeProductFromCart(cartId: String, productId: String): Resource<Boolean>

    suspend fun deleteCartProductsById(cartId: String): Resource<Boolean>

    suspend fun deleteCartProductsByOrderId(orderId: String): Resource<Boolean>

    fun getMainFeedProductQuantity(cartId: String, productId: String): Int

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

    /**
     * Delete old data from current start time
     * @return [Resource] of [Boolean] type.
     */
    suspend fun deletePastData(): Resource<Boolean>

}