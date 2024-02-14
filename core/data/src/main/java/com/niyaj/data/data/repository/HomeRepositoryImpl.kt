package com.niyaj.data.data.repository

import com.niyaj.data.repository.HomeRepository
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartOrder
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.model.filterByCategory
import com.niyaj.model.filterBySearch
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : HomeRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Main Feed Session")
    }

    override suspend fun getSelectedCartOrders(): Flow<CartOrder?> {
        return channelFlow {
            withContext(ioDispatcher) {
                val selectedCartOrder = realm.query<SelectedCartOrderEntity>().find().asFlow()

                selectedCartOrder.collect { changes ->
                    when (changes) {
                        is InitialResults -> {
                            if (changes.list.isNotEmpty()) {
                                send(changes.list.first().cartOrder?.toExternalModel())
                            } else {
                                send(null)
                            }
                        }

                        is UpdatedResults -> {
                            if (changes.list.isNotEmpty()) {
                                send(changes.list.first().cartOrder?.toExternalModel())
                            } else {
                                send(null)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun geMainFeedProducts(
        selectedCategory: String,
        searchText: String
    ): Flow<List<ProductWithFlowQuantity>> {
        return channelFlow {
            try {
                val products =
                    realm.query<ProductEntity>().sort("productId", Sort.ASCENDING).asFlow()
                val selectedCartOrder = realm.query<SelectedCartOrderEntity>().first().asFlow()

                selectedCartOrder.combine(products) { cartOrder, result ->
                    val orderId = cartOrder.obj?.cartOrder?.cartOrderId ?: ""
                    when (result) {
                        is InitialResults -> {
                            mapProductToProductWithQuantity(orderId, result.list)
                        }

                        is UpdatedResults -> {
                            mapProductToProductWithQuantity(orderId, result.list)
                        }
                    }
                }.collectLatest { list ->
                    val data = list.filter {
                        it.filterByCategory(selectedCategory)
                    }.filter {
                        it.filterBySearch(searchText)
                    }
                    send(data)
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    private suspend fun getQuantity(selectedCartOrder: String, productId: String): Flow<Int> {
        return channelFlow {
            val cart = realm.query<CartEntity>(
                "cartOrder.cartOrderId == $0 AND product.productId == $1",
                selectedCartOrder,
                productId
            ).first().asFlow()

            cart.collectLatest { cartResult ->
                when (cartResult) {
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
        selectedCartOrder: String,
        products: List<ProductEntity>
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