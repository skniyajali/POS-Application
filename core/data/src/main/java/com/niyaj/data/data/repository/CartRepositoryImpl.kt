package com.niyaj.data.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.searchAddOnItem
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : CartRepository {

    val realm = Realm.open(config)

    override suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val data = realm.query<AddOnItemEntity>()
                        .sort("addOnItemId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    data.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.searchAddOnItem(searchText) },
                        send = { send(it) },
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getAllDineInOrders(): Flow<List<CartItem>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<CartEntity>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.PROCESSING.name,
                        OrderType.DineIn.name
                    ).sort("cartId", Sort.DESCENDING).asFlow()

                    items.collectLatest { changes: ResultsChange<CartEntity> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(mapCartRealmToCartItem(changes.list))
                            }

                            else -> {
                                send(mapCartRealmToCartItem(changes.list))
                            }
                        }
                    }

                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getAllDineOutOrders(): Flow<List<CartItem>> {
        return channelFlow {
            try {
                val items = withContext(ioDispatcher) {
                    realm.query<CartEntity>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.PROCESSING.name,
                        OrderType.DineOut.name
                    ).sort("cartId", Sort.DESCENDING).asFlow()
                }

                items.collectLatest { changes: ResultsChange<CartEntity> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(mapCartRealmToCartItem(changes.list))
                        }

                        else -> {
                            send(mapCartRealmToCartItem(changes.list))
                        }
                    }
                }

            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    override suspend fun addProductToCart(
        cartOrderId: String,
        productId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val cartOrder =
                        this.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
                    val product =
                        this.query<ProductEntity>("productId == $0", productId).first().find()

                    if (cartOrder != null && product != null) {
                        if (cartOrder.cartOrderStatus != OrderStatus.PLACED.name) {

                            val cart = this.query<CartEntity>(
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
                                this.copyToRealm(CartEntity().apply {
                                    this.cartOrder = cartOrder
                                    this.product = product
                                    this.quantity = 1
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }, UpdatePolicy.ALL)
                            }

                            Resource.Success(true)
                        } else {
                            Resource.Error("Order already placed.")
                        }
                    } else {
                        Resource.Error("Unable to get cart order and product")
                    }
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add product to cart")
        }
    }

    override suspend fun removeProductFromCart(
        cartOrderId: String,
        productId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartOrder =
                    realm.query<CartOrderEntity>("cartOrderId = $0", cartOrderId).first().find()
                val product =
                    realm.query<ProductEntity>("productId == $0", productId).first().find()

                if (cartOrder != null && product != null) {
                    val doesCartAndProductAlreadyExists = realm.query<CartEntity>(
                        "cartOrder.cartOrderId == $0 AND product.productId == $1",
                        cartOrderId,
                        productId
                    ).first().find()

                    if (doesCartAndProductAlreadyExists != null) {
                        realm.write {
                            val cartProducts = realm.query<CartEntity>(
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
                        Resource.Error("Product not found")
                    }
                } else {
                    Resource.Error("Unable to find order and product")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update cart")
        }
    }

    override suspend fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        return withContext(ioDispatcher) {
            var totalPrice = 0
            var discountPrice = 0

            val cartOrder =
                realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
            val cartOrders =
                realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()

            if (cartOrder != null && cartOrders.isNotEmpty()) {
                if (cartOrder.doesChargesIncluded) {
                    val charges = realm.query<ChargesEntity>().find()

                    for (charge in charges) {
                        if (charge.isApplicable && cartOrder.orderType != OrderType.DineIn.name) {
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

            Pair(totalPrice, discountPrice)
        }
    }

    override suspend fun deletePastData(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartInterval = settingsRepository.getSetting().data?.cartDataDeletionInterval!!

                val startDate = getCalculatedStartDate("-$cartInterval")

                realm.write {
                    val carts = this.query<CartEntity>("updated_at < $0", startDate).find()

                    delete(carts)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart products")
        }
    }

    private fun getCartOrderById(cartOrderId: String): CartOrderEntity {
        return realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).find().first()
    }

    private suspend fun mapCartRealmToCartItem(carts: List<CartEntity>): List<CartItem> {
        return withContext(ioDispatcher) {
            val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

            groupedByOrder.map { groupedCartProducts ->
                if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                    val cartOrder = getCartOrderById(groupedCartProducts.key!!)

                    if (cartOrder.cartOrderStatus != OrderStatus.PLACED.name) {
                        CartItem(
                            cartOrderId = cartOrder.cartOrderId,
                            orderId = cartOrder.orderId,
                            orderType = OrderType.valueOf(cartOrder.orderType),
                            cartProducts = groupedCartProducts.value.map { cart ->
                                if (cart.product != null) {
                                    CartProductItem(
                                        productId = cart.product!!.productId,
                                        productName = cart.product!!.productName,
                                        productPrice = cart.product!!.productPrice,
                                        productQuantity = cart.quantity
                                    )
                                } else {
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
        }
    }
}