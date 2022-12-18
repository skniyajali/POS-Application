package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart_order.CartOrderRealmDao
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartOrderRepositoryImpl(
    private val cartOrderRealmDao: CartOrderRealmDao
) : CartOrderRepository {

    override fun getLastCreatedOrderId(): Long {
        return cartOrderRealmDao.getLastCreatedOrderId()
    }

    override suspend fun getAllCartOrders(): Flow<Resource<List<CartOrder>>> {
        return flow {
            cartOrderRealmDao.getAllCartOrders().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { cartOrder ->
                                CartOrder(
                                    cartOrderId = cartOrder._id,
                                    orderId = cartOrder.orderId,
                                    cartOrderType = cartOrder.orderType!!,
                                    cartOrderStatus = cartOrder.cartOrderStatus,
                                    customer = if(cartOrder.customer != null) {
                                        Customer(
                                            customerId = cartOrder.customer!!._id,
                                            customerPhone = cartOrder.customer!!.customerPhone,
                                            customerName = cartOrder.customer!!.customerName,
                                            customerEmail = cartOrder.customer!!.customerEmail,
                                            created_at = cartOrder.customer!!.created_at,
                                            updated_at = cartOrder.customer!!.updated_at,
                                        )
                                    } else null,
                                    address = cartOrder.address,
                                    addOnItems = cartOrder.addOnItems,
                                    doesChargesIncluded = cartOrder.doesChargesIncluded,
                                    created_at = cartOrder.created_at,
                                    updated_at = cartOrder.updated_at,
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get cart orders from database"))
                    }
                }
            }
        }
    }

    override suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?> {
        val result = cartOrderRealmDao.getCartOrderById(cartOrderId)

        return result.data?.let { cartOrder ->
            Resource.Success(
                CartOrder(
                    cartOrderId = cartOrder._id,
                    orderId = cartOrder.orderId,
                    cartOrderType = cartOrder.orderType!!,
                    cartOrderStatus = cartOrder.cartOrderStatus,
                    customer = if(cartOrder.customer != null) {
                        Customer(
                            customerId = cartOrder.customer!!._id,
                            customerPhone = cartOrder.customer!!.customerPhone,
                            customerName = cartOrder.customer!!.customerName,
                            customerEmail = cartOrder.customer!!.customerEmail,
                            created_at = cartOrder.customer!!.created_at,
                            updated_at = cartOrder.customer!!.updated_at,
                        )
                    } else null,
                    address = cartOrder.address,
                    addOnItems = cartOrder.addOnItems,
                    doesChargesIncluded = cartOrder.doesChargesIncluded,
                    created_at = cartOrder.created_at,
                    updated_at = cartOrder.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get cart order from database")
    }

    override suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean> {
        return cartOrderRealmDao.createNewOrder(newOrder)
    }

    override suspend fun updateCartOrder(newOrder: CartOrder, cartOrderId: String): Resource<Boolean> {
        return cartOrderRealmDao.updateCartOrder(newOrder, cartOrderId)
    }

    override suspend fun updateAddOnItem(addOnItemId: String, cartOrderId: String): Resource<Boolean> {
        return cartOrderRealmDao.updateAddOnItem(addOnItemId, cartOrderId)
    }

    override suspend fun deleteCartOrder(cartOrderId: String) : Resource<Boolean> {
        return cartOrderRealmDao.deleteCartOrder(cartOrderId)
    }

    override suspend fun placeOrder(cartOrderId: String): Resource<Boolean> {
        return cartOrderRealmDao.placeOrder(cartOrderId)
    }

    override suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean> {
        return cartOrderRealmDao.placeAllOrder(cartOrderIds)
    }

    override suspend fun getSelectedCartOrders(): Flow<SelectedCartOrderRealm?> {
        return cartOrderRealmDao.getSelectedCartOrders()
    }

    override suspend fun addSelectedCartOrder(cartOrderId: String): Boolean {
        return cartOrderRealmDao.addSelectedCartOrder(cartOrderId)
    }

    override suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean> {
        return cartOrderRealmDao.deleteCartOrders(deleteAll)
    }
}