package com.niyaj.popos.features.cart.data.repository

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.cart.domain.model.CartProduct
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.util.Constants.ADD_ON_EXCLUDE_ITEM_ONE
import com.niyaj.popos.util.Constants.ADD_ON_EXCLUDE_ITEM_TWO
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
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

    override suspend fun getAllCartProducts(): Flow<Resource<List<Cart>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val carts = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus == $0",
                        OrderStatus.Processing.orderStatus
                    ).sort("cartId", Sort.DESCENDING)

                    val items = carts.asFlow()

                    items.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
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

    override suspend fun getAllDineInOrders(): Flow<Resource<List<Cart>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val carts = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.Processing.orderStatus,
                        CartOrderType.DineIn.orderType
                    ).sort("cartId", Sort.DESCENDING)

                    val items = carts.asFlow()

                    items.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
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

    override suspend fun getAllDineOutOrders(): Flow<Resource<List<Cart>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val carts = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus == $0 AND cartOrder.orderType == $1",
                        OrderStatus.Processing.orderStatus,
                        CartOrderType.DineOut.orderType
                    ).sort("cartId", Sort.DESCENDING)

                    val items = carts.asFlow()

                    items.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
                                send(Resource.Loading(false))
                            }

                            else -> {
                                send(Resource.Success(mapCartRealmToCart(changes.list)))
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

    override suspend fun getCartByCartId(cartId: String): Resource<CartProduct?> {
        return try {

            val cartProducts = withContext(ioDispatcher) {
                realm.query<CartRealm>("cartId == $0", cartId).first().find()
            }

            Resource.Success(mapCartRealmToCartProduct(cartProducts))

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to find cart", null)
        }
    }

    override suspend fun getCartByCartOrderId(cartOrderId: String): Flow<Resource<List<CartProduct>>> {
        return channelFlow {
            try {
                val items: RealmResults<CartRealm> =
                    realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

                CoroutineScope(Dispatchers.Default).launch {
                    val itemsFlow = items.asFlow()
                    itemsFlow.collect { changes: ResultsChange<CartRealm> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartRealmToCartProducts(changes.list)))
                            }

                            is InitialResults -> {
                                send(Resource.Success(mapCartRealmToCartProducts(changes.list)))
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get cart products by order id", emptyList()))
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

    override suspend fun deleteCartById(cartId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val cartProducts = realm.query<CartRealm>("cartId == $0", cartId).first().find()

                if (cartProducts != null) {
                    realm.write {
                        findLatest(cartProducts)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find cart", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove product from cart", false)
        }
    }

    override suspend fun deleteCartByCartOrderId(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val cartProducts = this.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

                    delete(cartProducts)
                }
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart products by order id", false)
        }
    }

    override fun getMainFeedProductQuantity(cartOrderId: String, productId: String): Int {
        return try {
            realm.query<CartRealm>("cartOrder.cartOrderId == $0 && product.productId == $1", cartOrderId, productId)
                .first().find()?.quantity ?: 0
        } catch (e: Exception){
            0
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

                    // Todo: use dynamic fields for discount calculation.
                    if (addOnItem.itemName == ADD_ON_EXCLUDE_ITEM_ONE || addOnItem.itemName == ADD_ON_EXCLUDE_ITEM_TWO) {
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

    private fun mapCartRealmToCart(carts: List<CartRealm>): List<Cart>{
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        val data = groupedByOrder.map { groupedCartProducts ->
            if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                val cartOrder = getCartOrderById(groupedCartProducts.key!!)

                if (cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus) {
                    Cart(
                        cartOrder = cartOrder,
                        cartProducts = groupedCartProducts.value.map { cartProducts ->
                            CartProduct(
                                cartProductId = cartProducts.cartId,
                                orderId = cartOrder.cartOrderId,
                                product = cartProducts.product,
                                quantity = cartProducts.quantity
                            )
                        },
                        orderPrice = countTotalPrice(cartOrder.cartOrderId)
                    )
                } else {
                    Cart()
                }
            } else {
                Cart()
            }
        }

        return data
    }

    private fun mapCartRealmToCartProduct(cartRealm: CartRealm?): CartProduct? {
        return  if (cartRealm != null) {
            CartProduct(
                cartProductId = cartRealm.cartId,
                orderId = cartRealm.cartOrder?.cartOrderId!!,
                product = cartRealm.product,
                quantity = cartRealm.quantity
            )
        } else {
            null
        }
    }

    private fun mapCartRealmToCartProducts(cartRealm: List<CartRealm> = emptyList()): List<CartProduct> {
        return cartRealm.map { cartProducts ->
            CartProduct(
                cartProductId = cartProducts.cartId,
                orderId = cartProducts.cartOrder?.cartOrderId!!,
                product = cartProducts.product,
                quantity = cartProducts.quantity
            )
        }
    }

}