package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.model.CartProduct
import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.repository.CommonRepository
import com.niyaj.popos.domain.repository.OrderRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.order.OrderRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(
    private val orderRealmDao: OrderRealmDao,
    private val cartOrderRepository: CartOrderRepository,
    private val commonRepository: CommonRepository,
) : OrderRepository {

    override suspend fun getAllOrders(startDate: String, endDate: String): Flow<Resource<List<Cart>>> {
        return flow {
            orderRealmDao.getAllOrders(startDate, endDate).collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        var cart: List<Cart> = emptyList()

                        result.data?.let { cartProductsRealm ->
                            val groupedByOrder = cartProductsRealm.groupBy({ it.cartOrder?._id }) { it }

                            cart = groupedByOrder.map { groupedCartProducts ->
                                if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                                    val cartOrder = cartOrderRepository.getCartOrderById(groupedCartProducts.key!!).data
                                    if (cartOrder != null) {
                                        Cart(
                                            cartOrder = CartOrder(
                                                cartOrderId = cartOrder.cartOrderId,
                                                orderId = cartOrder.orderId,
                                                cartOrderType = cartOrder.cartOrderType,
                                                customer = if (cartOrder.customer != null)
                                                    Customer(
                                                        customerId = cartOrder.customer.customerId,
                                                        customerPhone = cartOrder.customer.customerPhone,
                                                        customerName = cartOrder.customer.customerName,
                                                        customerEmail = cartOrder.customer.customerEmail,
                                                        created_at = cartOrder.customer.created_at,
                                                        updated_at = cartOrder.customer.updated_at
                                                    ) else null,
                                                address = cartOrder.address,
                                                addOnItems = cartOrder.addOnItems,
                                                doesChargesIncluded = cartOrder.doesChargesIncluded,
                                                cartOrderStatus = cartOrder.cartOrderStatus,
                                                created_at = cartOrder.created_at,
                                                updated_at = cartOrder.updated_at
                                            ),
                                            cartProducts = groupedCartProducts.value.map { cartProducts ->
                                                CartProduct(
                                                    cartProductId = cartProducts._id,
                                                    orderId = cartOrder.cartOrderId,
                                                    product = Product(
                                                        productId = cartProducts.product?._id!!,
                                                        category = cartProducts.product?.category!!,
                                                        productName = cartProducts.product!!.productName,
                                                        productPrice = cartProducts.product!!.productPrice,
                                                        productAvailability = cartProducts.product!!.productAvailability
                                                            ?: true,
                                                        created_at = cartProducts.product!!.created_at,
                                                        updated_at = cartProducts.product?.updated_at
                                                    ),
                                                    quantity = cartProducts.quantity
                                                )
                                            },
                                            orderPrice = commonRepository.countTotalPrice(groupedCartProducts.key!!)
                                        )
                                    } else {
                                        Cart()
                                    }
                                } else {
                                    Cart()
                                }
                            }
                        }

                        emit(Resource.Success(cart))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all orders from database"))
                    }
                }


            }
        }
    }

    override suspend fun getOrderDetails(cartOrderId: String): Resource<Cart?> {
        val result = orderRealmDao.getOrderDetails(cartOrderId)
        var cart: Cart? = null
        
        return result.data?.let { cartRealmOrders ->
            val groupedByOrder = cartRealmOrders.groupBy({ it.cartOrder?._id }) { it }
            
            groupedByOrder.map { groupedCartProducts ->
                if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                    val cartOrder = cartOrderRepository.getCartOrderById(groupedCartProducts.key!!).data
                    if (cartOrder != null) {
                        cart = Cart(
                            cartOrder = CartOrder(
                                cartOrderId = cartOrder.cartOrderId,
                                orderId = cartOrder.orderId,
                                cartOrderType = cartOrder.cartOrderType,
                                customer = if (cartOrder.customer != null)
                                    Customer(
                                        customerId = cartOrder.customer.customerId,
                                        customerPhone = cartOrder.customer.customerPhone,
                                        customerName = cartOrder.customer.customerName,
                                        customerEmail = cartOrder.customer.customerEmail,
                                        created_at = cartOrder.customer.created_at,
                                        updated_at = cartOrder.customer.created_at
                                    ) else null,
                                address = cartOrder.address,
                                addOnItems = cartOrder.addOnItems,
                                doesChargesIncluded = cartOrder.doesChargesIncluded,
                                cartOrderStatus = cartOrder.cartOrderStatus,
                                created_at = cartOrder.created_at,
                                updated_at = cartOrder.updated_at
                            ),
                            cartProducts = groupedCartProducts.value.map { cartProducts ->
                                CartProduct(
                                    cartProductId = cartProducts._id,
                                    orderId = cartOrder.cartOrderId,
                                    product = Product(
                                        productId = cartProducts.product?._id!!,
                                        category = cartProducts.product?.category!!,
                                        productName = cartProducts.product!!.productName,
                                        productPrice = cartProducts.product!!.productPrice,
                                        productAvailability = cartProducts.product!!.productAvailability
                                            ?: true,
                                        created_at = cartProducts.product!!.created_at,
                                        updated_at = cartProducts.product?.updated_at
                                    ),
                                    quantity = cartProducts.quantity
                                )
                            },
                            orderPrice = commonRepository.countTotalPrice(groupedCartProducts.key!!)
                        )
                    } else {
                        cart = null
                    }
                } else {
                    cart = null
                }
            }

            Resource.Success(cart)
        } ?: Resource.Error(result.message ?: "Unable to get order details from database")
    }

    override suspend fun updateOrderStatus(cartOrderId: String, orderStatus: String): Resource<Boolean> {
        val result =  orderRealmDao.updateOrderStatus(cartOrderId, orderStatus)
        cartOrderRepository.getAllCartOrders()
        return result
    }

    override suspend fun deleteOrder(cartOrderId: String): Resource<Boolean> {
        return orderRealmDao.deleteOrder(cartOrderId)
    }

}