package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.model.CartProduct
import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.repository.CartRepository
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartRepositoryImpl(
    private val cartRealmDao: CartRealmDao,
    private val cartOrderRepository: CartOrderRepository,
    private val productRepository: ProductRepository,
) : CartRepository {

    override suspend fun getAllCartProducts(): Flow<Resource<List<Cart>>> {
        return flow {
            cartRealmDao.getAllCartProducts().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { cartProductsRealm ->
                                val groupedByOrder = cartProductsRealm.groupBy({ it.cartOrder?._id }) { it }
                                groupedByOrder.map { groupedCartProducts ->
                                    if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                                        val cartOrder = cartOrderRepository.getCartOrderById(groupedCartProducts.key!!).data


                                        if (cartOrder != null && cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus) {
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
                                                orderPrice = countTotalPrice(groupedCartProducts.key!!)
                                            )
                                        } else {
                                            Cart()
                                        }
                                    } else {
                                        Cart()
                                    }
                                }
                            }
                        ))
                    }

                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all cart products from database"))
                    }
                }
            }
        }
    }

    override suspend fun getAllDineInOrders(): Flow<Resource<List<Cart>>> {
        return flow {
            cartRealmDao.getAllDineInOrders().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { cartProductsRealm ->
                                val groupedByOrder = cartProductsRealm.groupBy({ it.cartOrder?._id }) { it }
                                groupedByOrder.map { groupedCartProducts ->
                                    if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                                        val cartOrder = cartOrderRepository.getCartOrderById(groupedCartProducts.key!!).data


                                        if (cartOrder != null && cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus) {
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
                                                orderPrice = countTotalPrice(groupedCartProducts.key!!)
                                            )
                                        } else {
                                            Cart()
                                        }
                                    } else {
                                        Cart()
                                    }
                                }
                            }
                        ))
                    }

                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all cart products from database"))
                    }
                }
            }
        }
    }

    override suspend fun getAllDineOutOrders(): Flow<Resource<List<Cart>>> {
        return flow {
            cartRealmDao.getAllDineOutOrders().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { cartProductsRealm ->
                                val groupedByOrder = cartProductsRealm.groupBy({ it.cartOrder?._id }) { it }

                                groupedByOrder.map { groupedCartProducts ->
                                    if (groupedCartProducts.key != null && groupedCartProducts.value.isNotEmpty()) {
                                        val cartOrder = cartOrderRepository.getCartOrderById(groupedCartProducts.key!!).data

                                        if (cartOrder != null && cartOrder.cartOrderStatus != OrderStatus.Placed.orderStatus) {
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
                                                orderPrice = countTotalPrice(groupedCartProducts.key!!)
                                            )
                                        } else {
                                            Cart()
                                        }
                                    } else {
                                        Cart()
                                    }
                                }
                            }
                        ))
                    }

                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all cart products from database"))
                    }
                }
            }
        }
    }

    override suspend fun getCartProductsById(cartProductId: String): Resource<CartProduct?> {
        val result = cartRealmDao.getCartProductsById(cartProductId)
        return result.data?.let { cartProducts ->
            Resource.Success(
                CartProduct(
                    cartProductId = cartProducts._id,
                    orderId = cartProducts.cartOrder?._id!!,
                    product = Product(
                        productId = cartProducts.product?._id!!,
                        category = cartProducts.product?.category!!,
                        productName = cartProducts.product!!.productName,
                        productPrice = cartProducts.product!!.productPrice,
                        productAvailability = cartProducts.product!!.productAvailability ?: true,
                        created_at = cartProducts.product!!.created_at,
                        updated_at = cartProducts.product?.updated_at
                    ),
                    quantity = cartProducts.quantity
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get data from database")
    }

    override suspend fun getCartProductsByOrderId(orderId: String): Flow<Resource<List<CartProduct>>> {
        return flow {
            cartRealmDao.getCartProductsByOrderId(orderId).collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let {
                                it.map { cartProducts ->
                                    CartProduct(
                                        cartProductId = cartProducts._id,
                                        orderId = cartProducts.cartOrder?._id!!,
                                        product = Product(
                                            productId = cartProducts.product?._id!!,
                                            category = cartProducts.product?.category!!,
                                            productName = cartProducts.product!!.productName,
                                            productPrice = cartProducts.product!!.productPrice,
                                            productAvailability = cartProducts.product!!.productAvailability ?: true,
                                            created_at = cartProducts.product!!.created_at,
                                            updated_at = cartProducts.product?.updated_at
                                        ),
                                        quantity = cartProducts.quantity
                                    )
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get Cart Product from database"))
                    }
                }

            }
        }
    }

    override suspend fun addProductToCart(cartId: String, productId: String): Resource<Boolean> {
        val result =  cartRealmDao.addProductToCart(cartId, productId)
        productRepository.getAllProducts()
        return result
    }

    override suspend fun removeProductFromCart(cartId: String, productId: String): Resource<Boolean> {
        return cartRealmDao.removeProductFromCart(cartId, productId)
    }

    override suspend fun deleteCartProductsById(cartId: String): Resource<Boolean> {
        return cartRealmDao.deleteCartProductsById(cartId)
    }

    override suspend fun deleteCartProductsByOrderId(orderId: String): Resource<Boolean> {
        return cartRealmDao.deleteCartProductsByOrderId(orderId)
    }

    override fun getMainFeedProductQuantity(cartId: String, productId: String): Int {
        return cartRealmDao.getMainFeedProductQuantity(cartId, productId)
    }

    override fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        return cartRealmDao.countTotalPrice(cartOrderId)
    }
}