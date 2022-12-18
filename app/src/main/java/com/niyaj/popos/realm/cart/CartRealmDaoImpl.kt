package com.niyaj.popos.realm.cart

import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.charges.ChargesRealm
import com.niyaj.popos.realm.product.ProductRealm
import com.niyaj.popos.util.getStartTime
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class CartRealmDaoImpl(
    config: RealmConfiguration
) : CartRealmDao {

    val realm = Realm.open(config)

    init {
        Timber.d("CartRealmDao Session")
    }

    private val _cartProducts = MutableStateFlow<List<CartRealm>>(listOf())
    val cartProducts = _cartProducts.asStateFlow()

    private val _cartProductsByOrder = MutableStateFlow<List<CartRealm>>(listOf())

    override suspend fun getAllCartProducts(): Flow<Resource<List<CartRealm>>> {
        return flow {
            try {
                emit(Resource.Loading(true))
                val items = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus == $0",
                    OrderStatus.Processing.orderStatus
                ).sort("_id", Sort.DESCENDING).asFlow()

                items.collect { changes: ResultsChange<CartRealm> ->
                    when (changes) {
                        is UpdatedResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }

                        else -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                    }
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unable to get cart products"))
            }
        }
    }

    override suspend fun getAllDineInOrders(): Flow<Resource<List<CartRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                    OrderStatus.Processing.orderStatus,
                    CartOrderType.DineIn.orderType
                ).sort("_id", Sort.DESCENDING)
                    .asFlow()

                items.collect { changes: ResultsChange<CartRealm> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }

                        else -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }
                    }
                }
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get cart products"))
            }
        }
    }

    override suspend fun getAllDineOutOrders(): Flow<Resource<List<CartRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val dineOutOrders = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                    OrderStatus.Processing.orderStatus,
                    CartOrderType.DineOut.orderType
                ).sort("_id", Sort.DESCENDING)

                val items = dineOutOrders.asFlow()

                items.collect { changes: ResultsChange<CartRealm> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get cart products"))
            }
        }
    }

    override suspend fun getCartProductsById(cartProductId: String): Resource<CartRealm?> {
        return try {
            val cart = realm.query<CartRealm>("_id == $0", cartProductId).first().find()
            Resource.Success(cart)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to find cart", null)
        }
    }

    override suspend fun getCartProductsByOrderId(orderId: String): Flow<Resource<List<CartRealm>>> {
        return flow {
            try {
                val items: RealmResults<CartRealm> =
                    realm.query<CartRealm>("cartOrder._id == $0", orderId).find()
                val itemsFlow = items.asFlow()
                itemsFlow.collect { changes: ResultsChange<CartRealm> ->
                    when (changes) {
                        is UpdatedResults -> {
                            _cartProductsByOrder.emit(changes.list)
                        }

                        is InitialResults -> {
                            _cartProductsByOrder.emit(changes.list)
                        }
                    }
                }

                emit(Resource.Success(_cartProductsByOrder.value))

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unable to get cart products by order id"))
            }
        }
    }

    override suspend fun addProductToCart(cartId: String, productId: String): Resource<Boolean> {
        return try {
            val doesCartAndProductAlreadyExists = realm.query<CartRealm>(
                "cartOrder._id == $0 AND product._id == $1",
                cartId,
                productId
            ).first().find()
            val cartOrder = realm.query<CartOrderRealm>("_id = $0", cartId).first().find()
            val product = realm.query<ProductRealm>("_id == $0", productId).first().find()

            if (cartOrder != null && product != null) {
                if (doesCartAndProductAlreadyExists == null) {

                    val cartProducts = CartRealm()

                    realm.write {

                        findLatest(cartOrder)?.also { cartProducts.cartOrder = it }

                        findLatest(product)?.also { cartProducts.product = it }

                        cartProducts.quantity = 1
                        cartProducts.updated_at = System.currentTimeMillis().toString()

                        this.copyToRealm(cartProducts)
                    }


                    Resource.Success(true)
                } else {
                    realm.write {
                        val cartProducts = realm.query<CartRealm>(
                            "cartOrder._id == $0 AND product._id == $1",
                            cartId,
                            productId
                        ).first().find()

                        if (cartProducts != null) {
                            findLatest(cartProducts)?.also {
                                it.updated_at = System.currentTimeMillis().toString()
                                it.quantity = doesCartAndProductAlreadyExists.quantity.plus(1)
                            }
                        }
                    }

                    Resource.Success(true)
                }
            } else {
                Resource.Error("Unable to find cart order and product", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add product into cart", false)
        }
    }

    override suspend fun removeProductFromCart(
        cartId: String,
        productId: String
    ): Resource<Boolean> {
        return try {
            val doesCartAndProductAlreadyExists = realm.query<CartRealm>(
                "cartOrder._id == $0 AND product._id == $1",
                cartId,
                productId
            ).first().find()
            val cartOrder = realm.query<CartOrderRealm>("_id = $0", cartId).first().find()
            val product = realm.query<ProductRealm>("_id == $0", productId).first().find()

            if (cartOrder != null && product != null) {
                if (doesCartAndProductAlreadyExists != null) {
                    realm.write {

                        val cartProducts = realm.query<CartRealm>(
                            "cartOrder._id == $0 AND product._id == $1",
                            cartId,
                            productId
                        ).first().find()

                        if (cartProducts != null) {
                            if (cartProducts.quantity == 1) {

                                findLatest(cartProducts)?.also { delete(it) }
                            } else {

                                findLatest(cartProducts)?.also {
                                    it.updated_at = System.currentTimeMillis().toString()
                                    it.quantity = cartProducts.quantity.minus(1)
                                }
                            }
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Product not found", false)
                }
            } else {
                Resource.Error("Unable to find order and product", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update cart", false)
        }
    }

    override suspend fun deleteCartProductsById(cartId: String): Resource<Boolean> {
        return try {
            realm.write {
                val cartProducts = realm.query<CartRealm>("_id == $0", cartId).find().first()

                delete(cartProducts)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove product from cart", false)
        }
    }

    override suspend fun deleteCartProductsByOrderId(orderId: String): Resource<Boolean> {
        return try {
            realm.write {
                val cartProducts: RealmResults<CartRealm> =
                    realm.query<CartRealm>("cartOrder._id == $0", orderId).find()

                delete(cartProducts)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart products by order id", false)
        }
    }

    override fun getMainFeedProductQuantity(cartId: String, productId: String): Int {
        return realm.query<CartRealm>("cartOrder._id == $0 && product._id == $1", cartId, productId)
            .first().find()?.quantity ?: 0
    }

    override fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder = realm.query<CartOrderRealm>("_id == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartRealm>("cartOrder._id == $0", cartOrderId).find()

        if (cartOrder != null && cartOrders.isNotEmpty()) {
            if (cartOrder.doesChargesIncluded) {
                val charges = realm.query<ChargesRealm>().find()
                for (charge in charges) {
                    if (charge.isApplicable && cartOrder.orderType != CartOrderType.DineIn.orderType) {
                        totalPrice += charge.chargesPrice
                    }
                }
            }

            if (cartOrder.addOnItems.isNotEmpty()) {
                for (addOnItem in cartOrder.addOnItems) {

                    totalPrice += addOnItem.itemPrice

                    // Todo: use dynamic fields for discount calculation.
                    if (addOnItem.itemName == "Masala" || addOnItem.itemName == "Mayonnaise") {
                        discountPrice += addOnItem.itemPrice
                    }
                }
            }

            for (cartOrder1 in cartOrders) {
                if (cartOrder1.product != null) {
                    totalPrice += cartOrder1.quantity.times(cartOrder1.product?.productPrice!!)
                }
            }
        }

        return Pair(totalPrice, discountPrice)
    }

    override suspend fun deletePastData(): Resource<Boolean> {
        return try {
            val startDate = getStartTime

            realm.write {
                val carts = realm.query<CartRealm>("updated_at <= $0", startDate).find()

                delete(carts)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart products", false)
        }
    }
}