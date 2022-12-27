package com.niyaj.popos.features.order.data.repository

import com.niyaj.popos.features.cart.domain.model.Cart
import com.niyaj.popos.features.cart.domain.model.CartProduct
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class OrderRepositoryImpl(
    config: RealmConfiguration
) : OrderRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Order Session")
    }

    override suspend fun getAllOrders(
        startDate: String,
        endDate: String
    ): Flow<Resource<List<Cart>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<CartRealm>(
                    "cartOrder.cartOrderStatus != $0 AND cartOrder.updatedAt >= $1 AND cartOrder.updatedAt <= $2",
                    OrderStatus.Processing.orderStatus,
                    startDate,
                    endDate,
                ).sort("cartId", Sort.DESCENDING).find()

                val itemFlow = items.asFlow()
                itemFlow.collect { changes: ResultsChange<CartRealm> ->
                    when (changes) {
                        is InitialResults -> {
                            emit(Resource.Success(mapCartRealmToCartList(changes.list)))
                            delay(50L)
                            emit(Resource.Loading(false))
                        }

                        is UpdatedResults -> {
                            emit(Resource.Success(mapCartRealmToCartList(changes.list)))
                            delay(50L)
                            emit(Resource.Loading(false))
                        }
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unable to get order", emptyList()))
            }
        }
    }

    override suspend fun getOrderDetails(cartOrderId: String): Resource<Cart?> {
        return try {
            val carts = realm.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

            if (carts.isNotEmpty()) {
                Resource.Success(mapCartRealmToCart(carts))
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to find cart order", null)
        }
    }

    override suspend fun updateOrderStatus(
        cartOrderId: String,
        orderStatus: String
    ): Resource<Boolean> {
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
                realm.write {
                    findLatest(cartOrder).also {
                        it?.cartOrderStatus = newOrderStatus
                        it?.updatedAt = System.currentTimeMillis().toString()
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
            realm.write {
                val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                if (cartOrder != null) {
                    val cartProducts: RealmResults<CartRealm> =
                        this.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

                    delete(cartProducts)
                    delete(cartOrder)
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

    private fun mapCartRealmToCartList(carts: List<CartRealm>): List<Cart>{
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        val data = groupedByOrder.map { groupedCartProducts ->
            if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                val cartOrder = getCartOrderById(groupedCartProducts.key!!)

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
        }

        return data
    }

    private fun mapCartRealmToCart(carts: List<CartRealm>): Cart? {
        val groupedByOrder = carts.groupBy { it.cartOrder?.cartOrderId }

        groupedByOrder.map { groupedCartProducts ->
            val cartOrder = getCartOrderById(groupedCartProducts.key!!)
            return Cart(
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
        }

        return null
    }

}