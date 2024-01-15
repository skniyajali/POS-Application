package com.niyaj.popos.features.cart.data.repository

import com.niyaj.popos.common.utils.getCalculatedStartDate
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.domain.model.CartItem
import com.niyaj.popos.features.cart.domain.model.CartProductItem
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class CartRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CartRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("CartRealmDao Session")
    }

    override suspend fun getAllDineInOrders() : Flow<Resource<List<CartItem>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.Processing.orderStatus,
                        CartOrderType.DineIn.orderType
                    ).sort("cartId", Sort.DESCENDING).asFlow()

                    items.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCartItem(changes.list)))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(mapCartRealmToCartItem(changes.list)))
                                send(Resource.Loading(false))
                            }
                        }
                    }

                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get cart products", emptyList()))
                }
            }
        }
    }

    override suspend fun getAllDineOutOrders() : Flow<Resource<List<CartItem>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.Processing.orderStatus,
                        CartOrderType.DineOut.orderType
                    ).sort("cartId", Sort.DESCENDING).asFlow()

                    items.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCartItem(changes.list)))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(mapCartRealmToCartItem(changes.list)))
                                send(Resource.Loading(false))
                            }
                        }
                    }

                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get cart products", emptyList()))
                }
            }
        }
    }

    override suspend fun addProductToCart(cartOrderId: String, productId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    val product = this.query<Product>("productId == $0", productId).first().find()

                    if (cartOrder != null && product != null) {
                        if (cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus){

                            val cart = this.query<CartRealm>(
                                "cartOrder.cartOrderId == $0 AND product.productId == $1",
                                cartOrderId,
                                productId
                            ).first().find()

                            if (cart != null) {
                                cart.cartOrder = cartOrder
                                cart.product = product
                                cart.quantity = cart.quantity.plus(1)
                                cart.updatedAt = System.currentTimeMillis().toString()
                            } else {
                                this.copyToRealm(CartRealm().apply {
                                    this.cartOrder = cartOrder
                                    this.product = product
                                    this.quantity = 1
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }, UpdatePolicy.ALL)
                            }

                            Resource.Success(true)
                        } else {
                            Resource.Error("Order already placed.", false)
                        }
                    }else {
                        Resource.Error("Unable to get cart order and product", false)
                    }
                }
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add product to cart", false)
        }
    }

    override suspend fun removeProductFromCart(cartOrderId: String, productId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val cartOrder = realm.query<CartOrder>("cartOrderId = $0", cartOrderId).first().find()
                val product = realm.query<Product>("productId == $0", productId).first().find()

                if (cartOrder != null && product != null) {
                    val doesCartAndProductAlreadyExists = realm.query<CartRealm>(
                        "cartOrder.cartOrderId == $0 AND product.productId == $1",
                        cartOrderId,
                        productId
                    ).first().find()

                    if (doesCartAndProductAlreadyExists != null) {
                        realm.write {
                            val cartProducts = realm.query<CartRealm>(
                                "cartOrder.cartOrderId == $0 AND product.productId == $1",
                                cartOrderId,
                                productId
                            ).first().find()

                            if (cartProducts != null) {
                                if (cartProducts.quantity == 1) {
                                    findLatest(cartProducts)?.also { delete(it) }
                                } else {
                                    findLatest(cartProducts)?.also {
                                        it.updatedAt = System.currentTimeMillis().toString()
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
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update cart", false)
        }
    }

    override fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

        if (cartOrder != null && cartOrders.isNotEmpty()) {
            if (cartOrder.doesChargesIncluded) {
                val charges = realm.query<Charges>().find()

                for (charge in charges) {
                    if (charge.isApplicable && cartOrder.orderType != CartOrderType.DineIn.orderType) {
                        totalPrice += charge.chargesPrice
                    }
                }
            }

            if (cartOrder.addOnItems.isNotEmpty()) {
                for (addOnItem in cartOrder.addOnItems) {

                    totalPrice += addOnItem.itemPrice

                    if (!addOnItem.isApplicable) {
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
            withContext(ioDispatcher) {
                val cartInterval = settingsRepository.getSetting().data?.cartDataDeletionInterval!!

                val startDate = getCalculatedStartDate("-$cartInterval")

                realm.write {
                    val carts = this.query<CartRealm>("updated_at < $0", startDate).find()

                    delete(carts)
                }
            }
            
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart products", false)
        }
    }

    private fun getCartOrderById(cartOrderId: String): CartOrder {
        return realm.query<CartOrder>("cartOrderId == $0", cartOrderId).find().first()
    }

    private fun mapCartRealmToCartItem(carts: List<CartRealm>): List<CartItem> {
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        val data = groupedByOrder.map { groupedCartProducts ->
            if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                val cartOrder = getCartOrderById(groupedCartProducts.key!!)

                if (cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus) {
                    CartItem(
                        cartOrderId = cartOrder.cartOrderId,
                        orderId = cartOrder.orderId,
                        orderType = cartOrder.orderType,
                        cartProducts = groupedCartProducts.value.map {cart ->
                            if (cart.product != null) {
                                CartProductItem(
                                    productId = cart.product!!.productId,
                                    productName = cart.product!!.productName,
                                    productPrice = cart.product!!.productPrice,
                                    productQuantity = cart.quantity
                                )
                            }else {
                                CartProductItem()
                            }
                        },
                        addOnItems = cartOrder.addOnItems.map { it.addOnItemId },
                        customerPhone = cartOrder.customer?.customerPhone,
                        customerAddress = cartOrder.address?.shortName,
                        updatedAt = cartOrder.createdAt,
                        orderPrice = countTotalPrice(cartOrder.cartOrderId)
                    )
                } else {
                    CartItem()
                }
            } else {
                CartItem()
            }
        }

        return data
    }
}