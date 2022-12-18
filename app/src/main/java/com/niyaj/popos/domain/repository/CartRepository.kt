package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.model.CartProduct
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    suspend fun getAllCartProducts(): Flow<Resource<List<Cart>>>

    suspend fun getAllDineInOrders(): Flow<Resource<List<Cart>>>

    suspend fun getAllDineOutOrders(): Flow<Resource<List<Cart>>>

    suspend fun getCartProductsById(cartProductId: String): Resource<CartProduct?>

    suspend fun getCartProductsByOrderId(orderId: String): Flow<Resource<List<CartProduct>>>

    suspend fun addProductToCart(cartId: String, productId: String): Resource<Boolean>

    suspend fun removeProductFromCart(cartId: String, productId: String): Resource<Boolean>

    suspend fun deleteCartProductsById(cartId: String): Resource<Boolean>

    suspend fun deleteCartProductsByOrderId(orderId: String): Resource<Boolean>

    fun getMainFeedProductQuantity(cartId: String, productId: String): Int

    fun countTotalPrice(cartOrderId: String): Pair<Int, Int>

}