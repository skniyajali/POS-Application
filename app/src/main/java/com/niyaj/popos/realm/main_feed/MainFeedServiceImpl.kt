package com.niyaj.popos.realm.main_feed

import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.CategoryRealm
import com.niyaj.popos.realm.product.ProductRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber

class MainFeedServiceImpl(
    config: RealmConfiguration
) : MainFeedService {

    val realm = Realm.open(config)

    init {
        Timber.d("Main Feed Session")
    }

    private val productWithQuantity = MutableStateFlow<List<ProductWithQuantityRealm>>(listOf())

    override suspend fun getSelectedCartOrders(): Flow<SelectedCartOrderRealm?> {
        val selectedCartOrder = realm.query(SelectedCartOrderRealm::class)
        val cartOrders = MutableStateFlow<SelectedCartOrderRealm?>(null)

        CoroutineScope(Dispatchers.Default).launch {
            val items = selectedCartOrder.asFlow()
            items.collect { changes ->
                when (changes){
                    is InitialResults -> {
                        if (changes.list.isNotEmpty()){
                            cartOrders.emit(changes.list.first())
                        }else {
                            cartOrders.emit(null)
                        }
                    }
                    is UpdatedResults -> {
                        if (changes.list.isNotEmpty()){
                            cartOrders.emit(changes.list.first())
                        }else {
                            cartOrders.emit(null)
                        }
                    }
                }
            }
        }

        return cartOrders
    }

    override suspend fun getAllCategories(): Flow<Resource<List<CategoryRealm>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items: RealmResults<CategoryRealm> = realm.query<CategoryRealm>().sort("_id", Sort.DESCENDING).find()
                // create a Flow from the Item collection, then add a listener to the Flow
                val itemsFlow = items.asFlow()
                itemsFlow.collect { changes: ResultsChange<CategoryRealm> ->
                    when (changes) {
                        // UpdatedResults means this change represents an update/insert/delete operation
                        is UpdatedResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                        else -> {
                            // types other than UpdatedResults are not changes -- ignore them
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                    }
                }
            }catch (e: Exception){
                emit(Resource.Error(e.message ?: "Unable to get all categories"))
            }
        }
    }

    override suspend fun getProductsWithQuantity(limit: Int): Flow<Resource<List<ProductWithQuantityRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val selectedCartOrder = realm.query<SelectedCartOrderRealm>().first().asFlow()
                val products = realm.query<ProductRealm>().limit(limit).asFlow()
                val cart = realm.query(
                    CartRealm::class,
                    "cartOrder.cartOrderStatus == $0",
                    OrderStatus.Processing.orderStatus,
                ).asFlow()

                selectedCartOrder.combine(products){ cartOrder, productsChanges ->
                    when(productsChanges){
                        is UpdatedResults -> {
                            ProductWithSelectedCartOrder(
                                product = productsChanges.list,
                                cartOrderId = when(cartOrder){
                                    is InitialObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    else -> ""
                                }
                            )
                        }

                        is InitialResults -> {
                            ProductWithSelectedCartOrder(
                                product = productsChanges.list,
                                cartOrderId = when(cartOrder){
                                    is InitialObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    else -> ""
                                }
                            )
                        }
                    }
                }.combine(cart){ cartOrder, cartChanged ->
                    when(cartChanged){
                        is InitialResults -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it._id) else 0
                                )
                            }
                        }
                        is UpdatedResults -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it._id) else 0
                                )
                            }
                        }
                        else -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = 0
                                )
                            }
                        }
                    }
                }.collect{
                    send(Resource.Success(it))
                    send(Resource.Loading(false))
                }

            }catch (e: Exception){
                Timber.e(e)

                send(Resource.Error(e.message ?: "Unable to get all products"))
            }
        }
    }

    override suspend fun getProductQuantity(cartOrderId: String, productId: String): Int {
        val cart =  realm.query<CartRealm>("cartOrder._id == $0 AND product._id == $1", cartOrderId, productId).first().find()

        return cart?.quantity ?: 0
    }

    override suspend fun getProducts(limit: Int): List<ProductWithQuantityRealm> {
        CoroutineScope(Dispatchers.Default).launch {
            supervisorScope {
                val selectedCartOrder = realm.query<SelectedCartOrderRealm>().first().asFlow()
                val products = realm.query<ProductRealm>().find().asFlow()
                val cart = realm.query(
                    CartRealm::class,
                    "cartOrder.cartOrderStatus == $0",
                    OrderStatus.Processing.orderStatus,
                ).asFlow()

                selectedCartOrder.combine(products){ cartOrder, productsChanges ->
                    when(productsChanges){
                        is UpdatedResults -> {
                            ProductWithSelectedCartOrder(
                                product = productsChanges.list,
                                cartOrderId = when(cartOrder){
                                    is InitialObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    else -> ""
                                }
                            )
                        }

                        is InitialResults -> {
                            ProductWithSelectedCartOrder(
                                product = productsChanges.list,
                                cartOrderId = when(cartOrder){
                                    is InitialObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!._id
                                        }else {
                                            ""
                                        }
                                    }
                                    else -> ""
                                }
                            )
                        }
                    }
                }.combine(cart){ cartOrder, cartChanged ->
                    when(cartChanged){
                        is InitialResults -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it._id) else 0
                                )
                            }
                        }
                        is UpdatedResults -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it._id) else 0
                                )
                            }
                        }
                        else -> {
                            cartOrder.product.map {
                                ProductWithQuantityRealm(
                                    productRealm = it,
                                    quantity = 0
                                )
                            }
                        }
                    }
                }.collect{
                    productWithQuantity.emit(it)
                }
            }
        }

        return productWithQuantity.value.toList()
    }
}

data class ProductWithSelectedCartOrder(
    val product: List<ProductRealm> = emptyList(),
    val cartOrderId: String = ""
)