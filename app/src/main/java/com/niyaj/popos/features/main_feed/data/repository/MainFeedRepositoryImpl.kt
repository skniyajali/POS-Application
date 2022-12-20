package com.niyaj.popos.features.main_feed.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.product.domain.model.Product
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

class MainFeedRepositoryImpl(
    config: RealmConfiguration
) : MainFeedRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Main Feed Session")
    }

    private val productWithQuantity = MutableStateFlow<List<ProductWithQuantity>>(listOf())

    override suspend fun getSelectedCartOrders(): Flow<String?> {
        val selectedCartOrder = realm.query(SelectedCartOrder::class)
        val cartOrders = MutableStateFlow<String?>(null)

        CoroutineScope(Dispatchers.Default).launch {
            val items = selectedCartOrder.asFlow()
            items.collect { changes ->
                when (changes){
                    is InitialResults -> {
                        if (changes.list.isNotEmpty()){
                            cartOrders.emit(changes.list.first().cartOrder?.cartOrderId)
                        }else {
                            cartOrders.emit(null)
                        }
                    }
                    is UpdatedResults -> {
                        if (changes.list.isNotEmpty()){
                            cartOrders.emit(changes.list.first().cartOrder?.cartOrderId)
                        }else {
                            cartOrders.emit(null)
                        }
                    }
                }
            }
        }

        return cartOrders
    }

    override suspend fun getAllCategories(): Flow<Resource<List<Category>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items: RealmResults<Category> = realm.query<Category>().sort("categoryId", Sort.DESCENDING).find()
                // create a Flow from the Item collection, then add a listener to the Flow
                val itemsFlow = items.asFlow()
                itemsFlow.collect { changes: ResultsChange<Category> ->
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

    override suspend fun getProductsWithQuantity(): Flow<Resource<List<ProductWithQuantity>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val selectedCartOrder = realm.query<SelectedCartOrder>().first().asFlow()
                val products = realm.query<Product>().asFlow()
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
                                            cartOrder.obj.cartOrder!!.cartOrderId
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!.cartOrderId
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
                                            cartOrder.obj.cartOrder!!.cartOrderId
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!.cartOrderId
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
                                ProductWithQuantity(
                                    product = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it.productId) else 0
                                )
                            }
                        }
                        is UpdatedResults -> {
                            cartOrder.product.map {
                                ProductWithQuantity(
                                    product = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it.productId) else 0
                                )
                            }
                        }
                        else -> {
                            cartOrder.product.map {
                                ProductWithQuantity(
                                    product = it,
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
        val cart =  realm.query<CartRealm>("cartOrder.cartOrderId == $0 AND product.productId == $1", cartOrderId, productId).first().find()

        return cart?.quantity ?: 0
    }

    override suspend fun getProducts(limit: Int): List<ProductWithQuantity> {
        CoroutineScope(Dispatchers.Default).launch {
            supervisorScope {
                val selectedCartOrder = realm.query<SelectedCartOrder>().first().asFlow()
                val products = realm.query<Product>().find().asFlow()
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
                                            cartOrder.obj.cartOrder!!.cartOrderId
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!.cartOrderId
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
                                            cartOrder.obj.cartOrder!!.cartOrderId
                                        }else {
                                            ""
                                        }
                                    }
                                    is UpdatedObject -> {
                                        if(cartOrder.obj.cartOrder != null){
                                            cartOrder.obj.cartOrder!!.cartOrderId
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
                                ProductWithQuantity(
                                    product = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it.productId) else 0
                                )
                            }
                        }
                        is UpdatedResults -> {
                            cartOrder.product.map {
                                ProductWithQuantity(
                                    product = it,
                                    quantity = if(cartChanged.list.isNotEmpty() && cartOrder.cartOrderId.isNotEmpty()) getProductQuantity(cartOrder.cartOrderId, it.productId) else 0
                                )
                            }
                        }
                        else -> {
                            cartOrder.product.map {
                                ProductWithQuantity(
                                    product = it,
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
    val product: List<Product> = emptyList(),
    val cartOrderId: String = ""
)