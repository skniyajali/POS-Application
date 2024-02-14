package com.niyaj.data.data.repository

import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.getCalculatedEndDate
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.common.utils.getEndTime
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.OrderRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.DineInOrder
import com.niyaj.model.DineOutOrder
import com.niyaj.model.OrderDetail
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.searchDineInOrder
import com.niyaj.model.searchDineOutOrder
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

class OrderRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : OrderRepository {

    val realm = Realm.open(config)

    override suspend fun getAllCharges(): Flow<List<Charges>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val charges = realm.query<ChargesEntity>()
                        .sort("chargesId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    charges.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it },
                        send = { send(it) }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getOrderDetails(cartOrderId: String): OrderDetail? {
        return try {
            val carts = withContext(ioDispatcher) {
                realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()
            }

            if (carts.isNotEmpty()) {
                mapCartRealmToCart(carts)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateOrderStatus(
        cartOrderId: String,
        orderStatus: OrderStatus
    ): Resource<Boolean> {
        return try {
            val cartOrder = withContext(ioDispatcher) {
                realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
            }

            if (cartOrder != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(cartOrder).also {
                            it?.cartOrderStatus = orderStatus.name
                            it?.updatedAt = System.currentTimeMillis().toString()
                        }
                    }

                    if (orderStatus == OrderStatus.PROCESSING) {
                        addSelectedCartOrder(cartOrderId)
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update order status")
        }
    }

    override suspend fun deleteOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val cartOrder =
                        this.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
                    if (cartOrder != null) {
                        val cartProducts: RealmResults<CartEntity> =
                            this.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId)
                                .find()

                        delete(cartProducts)
                        delete(cartOrder)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete order")
        }
    }

    private fun getCartOrderById(cartOrderId: String): CartOrderEntity {
        return realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).find().first()
    }

    private fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder =
            realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()

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

        return Pair(totalPrice, discountPrice)
    }

    private fun mapCartRealmToCart(carts: List<CartEntity>): OrderDetail? {
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        groupedByOrder.map { groupedCartProducts ->
            val cartOrder = getCartOrderById(groupedCartProducts.key!!)
            return OrderDetail(
                cartOrder = cartOrder.toExternalModel(),
                orderedProducts = groupedCartProducts.value.map { cartProducts ->
                    if (cartProducts.product != null) {
                        CartProductItem(
                            productId = cartProducts.product!!.productId,
                            productName = cartProducts.product!!.productName,
                            productPrice = cartProducts.product!!.productPrice,
                            productQuantity = cartProducts.quantity
                        )
                    } else {
                        CartProductItem()
                    }
                },
                orderPrice = countTotalPrice(cartOrder.cartOrderId)
            )
        }

        return null
    }

    private suspend fun addSelectedCartOrder(cartOrderId: String): Boolean {
        return try {
            withContext(ioDispatcher) {
                val order =
                    realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()

                if (order != null && order.cartOrderStatus != OrderStatus.PLACED.name) {
                    realm.write {
                        val selectedCartOrder = this.query<SelectedCartOrderEntity>(
                            "selectedCartId == $0",
                            Constants.SELECTED_CART_ORDER_ID
                        ).first().find()

                        if (selectedCartOrder != null) {
                            findLatest(order)?.let {
                                selectedCartOrder.cartOrder = it
                            }
                        } else {
                            val cartOrder = SelectedCartOrderEntity()
                            findLatest(order)?.let {
                                cartOrder.cartOrder = it
                            }

                            this.copyToRealm(cartOrder, UpdatePolicy.ALL)
                        }
                    }
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun getDineInOrders(
        searchText: String,
        date: String
    ): Flow<List<DineInOrder>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val startDate = if (date.isNotEmpty()) {
                        getCalculatedStartDate(date = date)
                    } else getStartTime

                    val endDate = if (date.isNotEmpty()) {
                        getCalculatedEndDate(date = date)
                    } else getEndTime

                    val items = withContext(ioDispatcher) {
                        realm.query<CartOrderEntity>(
                            "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                            OrderStatus.PROCESSING.name,
                            startDate,
                            endDate,
                            OrderType.DineIn.name
                        ).sort("updatedAt", Sort.DESCENDING).find().asFlow()
                    }

                    items.collectLatest { changes ->
                        when (changes) {
                            is InitialResults -> {
                                val data = mapCartOrderToDineInOrders(changes.list).filter {
                                    it.searchDineInOrder(searchText)
                                }
                                send(data)
                            }

                            is UpdatedResults -> {
                                val data = mapCartOrderToDineInOrders(changes.list).filter {
                                    it.searchDineInOrder(searchText)
                                }
                                send(data)
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getDineOutOrders(
        searchText: String,
        date: String
    ): Flow<List<DineOutOrder>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val startDate = if (date.isNotEmpty()) {
                        getCalculatedStartDate(date = date)
                    } else getStartTime

                    val endDate = if (date.isNotEmpty()) {
                        getCalculatedEndDate(date = date)
                    } else getEndTime

                    val items = realm.query<CartOrderEntity>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.PROCESSING.name,
                        startDate,
                        endDate,
                        OrderType.DineOut.name
                    ).sort("updatedAt", Sort.DESCENDING).find().asFlow()


                    items.collect { changes ->
                        when (changes) {
                            is InitialResults -> {
                                val data = mapCartOrderToDineOutOrders(changes.list)
                                    .filter { it.searchDineOutOrder(searchText) }

                                send(data)
                            }

                            is UpdatedResults -> {
                                val data = mapCartOrderToDineOutOrders(changes.list)
                                    .filter { it.searchDineOutOrder(searchText) }

                                send(data)
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }


    private suspend fun mapCartOrderToDineOutOrders(data: List<CartOrderEntity>): List<DineOutOrder> {
        val result = withContext(ioDispatcher) {
            data.map { cartOrder ->
                val getPrice = countTotalPrice(cartOrder.cartOrderId)
                val price = getPrice.first.minus(getPrice.second).toString()

                DineOutOrder(
                    cartOrderId = cartOrder.cartOrderId,
                    orderId = cartOrder.orderId,
                    customerPhone = cartOrder.customer?.customerPhone!!,
                    customerAddress = cartOrder.address?.shortName!!,
                    totalAmount = price,
                    updatedAt = cartOrder.updatedAt!!
                )
            }
        }

        return result
    }

    private suspend fun mapCartOrderToDineInOrders(data: List<CartOrderEntity>): List<DineInOrder> {
        val result = withContext(ioDispatcher) {
            data.map { cartOrder ->
                val getPrice = countTotalPrice(cartOrder.cartOrderId)
                val price = getPrice.first.minus(getPrice.second).toString()

                DineInOrder(
                    cartOrderId = cartOrder.cartOrderId,
                    orderId = cartOrder.orderId,
                    totalAmount = price,
                    updatedAt = cartOrder.updatedAt!!
                )
            }
        }

        return result
    }

}