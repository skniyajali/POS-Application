package com.niyaj.popos.features.main_feed.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.main_feed.domain.model.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.product.domain.model.Product
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainFeedRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MainFeedRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Main Feed Session")
    }

    override suspend fun getSelectedCartOrders(): Flow<CartOrder?> {
        return channelFlow {
            withContext(ioDispatcher) {
                val selectedCartOrder = realm.query<SelectedCartOrder>().find().asFlow()

                selectedCartOrder.collect { changes ->
                    when (changes) {
                        is InitialResults -> {
                            if (changes.list.isNotEmpty()) {
                                send(changes.list.first().cartOrder)
                            }else {
                                send(null)
                            }
                        }

                        is UpdatedResults -> {
                            if (changes.list.isNotEmpty()) {
                                send(changes.list.first().cartOrder)
                            }else {
                                send(null)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun geMainFeedProducts() : Flow<Resource<List<ProductWithFlowQuantity>>> {
        return channelFlow {
            try {
                val products = realm.query<Product>().sort("productId", Sort.ASCENDING).asFlow()
                val selectedCartOrder = realm.query<SelectedCartOrder>().first().asFlow()

                selectedCartOrder.combine(products) { cartOrder, result ->
                    val orderId = cartOrder.obj?.cartOrder?.cartOrderId ?: ""
                    when (result){
                        is InitialResults -> {
                            mapProductToProductWithQuantity(orderId, result.list)
                        }

                        is UpdatedResults -> {
                            mapProductToProductWithQuantity(orderId, result.list)
                        }
                    }
                }.collectLatest {
                    send(Resource.Success(it))
                    send(Resource.Loading(false))
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get product"))
            }
        }
    }

    private suspend fun getQuantity(selectedCartOrder: String, productId: String): Flow<Int> {
        return channelFlow {
            val cart = realm.query<CartRealm>(
                "cartOrder.cartOrderId == $0 AND product.productId == $1",
                selectedCartOrder,
                productId
            ).first().asFlow()

            cart.collectLatest { cartResult ->
                when(cartResult){
                    is InitialObject -> {
                        send(cartResult.obj.quantity)
                    }
                    is UpdatedObject -> {
                        send(cartResult.obj.quantity)
                    }
                    else -> {
                        send(0)
                    }
                }
            }
        }
    }

    private suspend fun mapProductToProductWithQuantity(
        selectedCartOrder : String,
        products : List<Product>
    ): List<ProductWithFlowQuantity> {
        val data = products.map { product ->
            ProductWithFlowQuantity(
                categoryId = product.category?.categoryId!!,
                productId = product.productId,
                productName = product.productName,
                productPrice = product.productPrice,
                quantity = getQuantity(selectedCartOrder, product.productId).distinctUntilChanged()
            )
        }

        return data
    }
}