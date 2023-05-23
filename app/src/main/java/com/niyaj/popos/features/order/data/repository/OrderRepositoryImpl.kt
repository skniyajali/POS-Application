package com.niyaj.popos.features.order.data.repository

import com.niyaj.popos.features.cart.domain.model.CartProductItem
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.domain.model.DineOutOrder
import com.niyaj.popos.features.order.domain.model.OrderDetail
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class OrderRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OrderRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Order Session")
    }

    override suspend fun getOrderDetails(cartOrderId: String): Resource<OrderDetail?> {
        return try {
            val carts = withContext(ioDispatcher) {
                realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()
            }

            if (carts.isNotEmpty()) {
                Resource.Success(mapCartRealmToCart(carts))
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to find cart order", null)
        }
    }

    override suspend fun updateOrderStatus(cartOrderId : String, orderStatus : String): Resource<Boolean> {
        return try {
            val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()

            val newOrderStatus = if (orderStatus == OrderStatus.Delivered.orderStatus) {
                if (cartOrder?.cartOrderStatus == orderStatus) {
                    OrderStatus.Placed.orderStatus
                } else {
                    OrderStatus.Delivered.orderStatus
                }
            } else {
                orderStatus
            }

            if (cartOrder != null) {
                withContext(ioDispatcher){
                    realm.write {
                        findLatest(cartOrder).also {
                            it?.cartOrderStatus = newOrderStatus
                            it?.updatedAt = System.currentTimeMillis().toString()
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find order", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update order status", false)
        }
    }

    override suspend fun deleteOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    if (cartOrder != null) {
                        val cartProducts: RealmResults<CartRealm> =
                            this.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

                        delete(cartProducts)
                        delete(cartOrder)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete order", false)
        }
    }

    private fun getCartOrderById(cartOrderId: String): CartOrder {
        return realm.query<CartOrder>("cartOrderId == $0", cartOrderId).find().first()
    }

    private fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
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

    private fun mapCartRealmToCart(carts: List<CartRealm>): OrderDetail? {
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        groupedByOrder.map { groupedCartProducts ->
            val cartOrder = getCartOrderById(groupedCartProducts.key!!)
            return OrderDetail(
                cartOrder = cartOrder,
                orderedProducts = groupedCartProducts.value.map { cartProducts ->
                    if (cartProducts.product != null) {
                        CartProductItem(
                            productId = cartProducts.product!!.productId,
                            productName = cartProducts.product!!.productName,
                            productPrice = cartProducts.product!!.productPrice,
                            productQuantity = cartProducts.quantity
                        )
                    }else {
                        CartProductItem()
                    }
                },
                orderPrice = countTotalPrice(cartOrder.cartOrderId)
            )
        }

        return null
    }


    override suspend fun getDineInOrders(
        startDate : String,
        endDate : String
    ) : Flow<Resource<List<DineInOrder>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val items = realm.query<CartOrder>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.Processing.orderStatus,
                        startDate,
                        endDate,
                        CartOrderType.DineIn.orderType
                    ).sort("updatedAt", Sort.DESCENDING).find().asFlow()

                    items.collect { changes ->
                        when (changes) {
                            is InitialResults -> {
                                send(Resource.Success(mapCartOrderToDineInOrders(changes.list)))
                                send(Resource.Loading(false))
                            }

                            is UpdatedResults -> {
                                send(Resource.Success(mapCartOrderToDineInOrders(changes.list)))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get order", emptyList()))
                }
            }
        }
    }

    override suspend fun getDineOutOrders(
        startDate : String,
        endDate : String
    ) : Flow<Resource<List<DineOutOrder>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val items = realm.query<CartOrder>(
                        "cartOrderStatus != $0 AND updatedAt >= $1 AND updatedAt <= $2 AND orderType == $3",
                        OrderStatus.Processing.orderStatus,
                        startDate,
                        endDate,
                        CartOrderType.DineOut.orderType
                    ).sort("updatedAt", Sort.DESCENDING).find().asFlow()


                    items.collect { changes ->
                        when (changes) {
                            is InitialResults -> {
                                send(Resource.Success(mapCartOrderToDineOutOrders(changes.list)))
                                send(Resource.Loading(false))
                            }

                            is UpdatedResults -> {
                                send(Resource.Success(mapCartOrderToDineOutOrders(changes.list)))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get order", emptyList()))
                }
            }
        }
    }


    private suspend fun mapCartOrderToDineOutOrders(data: List<CartOrder>): List<DineOutOrder> {
        val result = withContext(ioDispatcher) {
            data.map {cartOrder ->
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

    private suspend fun mapCartOrderToDineInOrders(data: List<CartOrder>): List<DineInOrder> {
        val result = withContext(ioDispatcher) {
            data.map {cartOrder ->
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