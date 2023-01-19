package com.niyaj.popos.features.cart.data.repository

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart_order.data.repository.CartOrderRepositoryImpl
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.product.data.repository.ProductRepositoryImpl
import com.niyaj.popos.features.product.domain.model.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import timber.log.Timber


@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CartRepositoryImplTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var categoryRepository: CategoryRepositoryImpl
    private lateinit var productRepository: ProductRepositoryImpl
    private lateinit var cartOrderRepository: CartOrderRepositoryImpl
    private lateinit var customerRepository: CustomerRepositoryImpl
    private lateinit var addressRepository: AddressRepositoryImpl
    private lateinit var repository: CartRepositoryImpl

    private var category: Category? = null
    private var product: Product? = null
    private var newProduct: Product? = null
    private var cartOrder: CartOrder? = null
    private var customer: Customer? = null
    private var address: Address? = null

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)
        categoryRepository = CategoryRepositoryImpl(config, dispatcher)
        productRepository = ProductRepositoryImpl(config, dispatcher)
        customerRepository = CustomerRepositoryImpl(config, dispatcher)
        addressRepository = AddressRepositoryImpl(config, dispatcher)
        settingsRepository = SettingsRepositoryImpl(config, dispatcher)
        cartOrderRepository = CartOrderRepositoryImpl(config, settingsRepository, this, dispatcher)
        repository = CartRepositoryImpl(config, settingsRepository, dispatcher)

        category = getOrCreateCategory()
        delay(1000L)

        product = getOrCreateProduct()
        delay(1000L)

        address = getOrCreateAddress()
        delay(1000L)

        customer = getOrCreateCustomer()
        delay(1000L)

        newProduct = getOrCreateProduct(productId = "2222", productName = "Another Product")
        delay(1000L)

        cartOrder = getOrCreateCartOrder()
        delay(1000L)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_add_products_to_cart_with_invalid_cart_order_and_product_id_return_false() = runTest {
        val result = repository.addProductToCart("89n", "89jk")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to get cart order and product")
    }

    @Test
    fun b_add_products_to_cart_with_valid_cart_order_and_product_id_return_true() = runTest {
        val result = repository.addProductToCart(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_cart_products_by_cart_order_id_return_true() = runTest {
        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(1)
                    assertThat(resource.data?.first()?.orderId).isEqualTo(cartOrder?.cartOrderId)
                    assertThat(resource.data?.first()?.quantity).isEqualTo(1)
                    assertThat(resource.data?.first()?.product?.productId).isEqualTo(product?.productId)
                    assertThat(resource.data?.first()?.product?.productName).isEqualTo(product?.productName)
                    assertThat(resource.data?.first()?.product?.productPrice).isEqualTo(product?.productPrice)
                }
                else -> {}
            }
        }
    }

    @Test
    fun d_remove_cart_product_with_invalid_cart_order_and_product_id_return_false() = runTest {
        val result = repository.removeProductFromCart("89789", "90jo")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find order and product")
    }

    @Test
    fun e_remove_cart_product_with_valid_cart_order_and_invalid_product_id_return_false() = runTest {
        val result = repository.removeProductFromCart(cartOrder?.cartOrderId ?: "", newProduct?.productId ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find order and product")
    }

    @Test
    fun f_remove_cart_product_with_valid_cart_order_and_valid_product_id_return_true() = runTest {
        val result = repository.removeProductFromCart(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(0)
                }

                else -> {}
            }
        }
    }

    @Test
    fun g_get_main_feed_product_quantity_with_invalid_data_return_0() = runTest {
        val result = repository.getMainFeedProductQuantity("9078d", "89shd")

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun h_get_main_feed_product_quantity_with_valid_data_return_0() = runTest {
        val result = repository.getMainFeedProductQuantity(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        assertThat(result).isEqualTo(0)
    }

    @Test
    fun i_get_main_feed_product_quantity_with_valid_data_return_1() = runTest {
        val result = repository.addProductToCart(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()


        val result1 = repository.getMainFeedProductQuantity(cartOrder?.cartOrderId ?: "", product?.productId ?: "")
        assertThat(result1).isEqualTo(1)

        repository.addProductToCart(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        val result2 = repository.getMainFeedProductQuantity(cartOrder?.cartOrderId ?: "", product?.productId ?: "")
        assertThat(result2).isEqualTo(2)
    }

    @Test
    fun j_get_cart_by_invalid_id_return_null() = runTest {
        val result = repository.getCartByCartId("908dd")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun k_get_cart_by_valid_id_return_cart_products() = runTest {
        var id: String? = null

        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(1)
                    assertThat(resource.data?.first()?.quantity).isEqualTo(2)
                    id = resource.data?.first()?.cartProductId
                }

                else -> {}
            }
        }

        val result1 = repository.getCartByCartId(id ?: "")
        assertThat(result1.data).isNotNull()
        assertThat(result1.data?.cartProductId).isEqualTo(id)
        assertThat(result1.data?.quantity).isEqualTo(2)
        assertThat(result1.data?.orderId).isEqualTo(cartOrder?.cartOrderId)
        assertThat(result1.data?.product?.productId).isEqualTo(product?.productId)
        assertThat(result1.data?.product?.productName).isEqualTo(product?.productName)
        assertThat(result1.data?.product?.productPrice).isEqualTo(product?.productPrice)
    }

    @Test
    fun l_delete_by_cart_id_with_invalid_id_return_false() = runTest {
        val result = repository.deleteCartById("90890")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find cart")
    }

    @Test
    fun m_delete_by_cart_id_with_valid_data_return_true() = runTest {
        var id: String? = null

        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(1)
                    assertThat(resource.data?.first()?.quantity).isEqualTo(2)
                    id = resource.data?.first()?.cartProductId
                }

                else -> {}
            }
        }

        val result = repository.deleteCartById(id ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result1 = repository.getCartByCartId(id ?: "")

        assertThat(result1.data).isNull()
        assertThat(result1.message).isNull()
    }

    @Test
    fun n_delete_cart_by_cart_order_id_return_true() = runTest {
        val result = repository.addProductToCart(cartOrder?.cartOrderId ?: "", product?.productId ?: "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        repository.addProductToCart(cartOrder?.cartOrderId ?: "", newProduct?.productId ?: "")

        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(2)
                }

                else -> {}
            }
        }

        val result1 = repository.deleteCartByCartOrderId(cartOrder?.cartOrderId ?: "")
        assertThat(result1.data).isTrue()
        assertThat(result1.message).isNull()

        repository.getCartByCartOrderId(cartOrder?.cartOrderId ?: "").onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(0)
                }

                else -> {}
            }
        }

    }

    @Test
    fun o_get_dine_in_orders() = runTest {
        val result = createNewDineInOrdersAndAddProducts()
        assertThat(result).isTrue()

        repository.getAllDineInOrders().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(5)

                    resource.data?.let {
                        it.forEach { cart ->
                            assertThat(cart.cartOrder?.orderType).isEqualTo(CartOrderType.DineIn.orderType)
                            cart.cartProducts.forEach { product ->
                                assertThat(product.quantity).isEqualTo(1)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    @Test
    fun p_get_dine_out_orders() = runTest {
        val result = createNewDineOutOrdersAndAddProducts()
        assertThat(result).isTrue()

        repository.getAllDineOutOrders().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(5)

                    resource.data?.let {
                        it.forEach { cart ->
                            assertThat(cart.cartOrder?.orderType).isEqualTo(CartOrderType.DineOut.orderType)
                            assertThat(cart.cartOrder?.address?.addressId).isEqualTo(address?.addressId)
                            assertThat(cart.cartOrder?.address?.addressName).isEqualTo(address?.addressName)
                            assertThat(cart.cartOrder?.customer?.customerPhone).isEqualTo(customer?.customerPhone)
                            assertThat(cart.cartOrder?.customer?.customerId).isEqualTo(customer?.customerId)

                            cart.cartProducts.forEach { product ->
                                assertThat(product.quantity).isEqualTo(2)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }


    private fun createNewDineInOrdersAndAddProducts(): Boolean {
        return try {
            val cartOrders = mutableListOf<CartOrder>()

            (1..5).forEach {  c ->
                cartOrders.add(
                    CartOrder(
                        cartOrderId = c.toString(),
                        orderId = c.toString(),
                    )
                )
            }

            runTest {
                cartOrders.forEach { cartOrder ->
                    val result = repository.addProductToCart(cartOrder.cartOrderId, product?.productId ?: "")

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                    assertThat(result.message).isNull()
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }

    private fun createNewDineOutOrdersAndAddProducts(): Boolean {
        return try {
            val cartOrders = mutableListOf<CartOrder>()

            (6..10).forEach {  c ->
                cartOrders.add(
                    CartOrder(
                        cartOrderId = c.toString(),
                        orderId = c.toString(),
                        orderType = CartOrderType.DineOut.orderType,
                        customer = customer,
                        address = address,
                    )
                )
            }

            runTest {
                cartOrders.forEach { cartOrder ->
                    val result = repository.addProductToCart(cartOrder.cartOrderId, newProduct?.productId ?: "")

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                    assertThat(result.message).isNull()

                    repository.addProductToCart(cartOrder.cartOrderId, newProduct?.productId ?: "")
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }

    private fun getOrCreateCategory(categoryId: String = "1111", categoryName: String = "New Category"): Category? {
        var category: Category? = null

        runTest {
            try {
                val result = categoryRepository.getCategoryById(categoryId)

                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                category = result.data
            }catch (e: AssertionError) {
                val newCategory = Category(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    categoryAvailability = true,
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = categoryRepository.createNewCategory(newCategory)

                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()
                assertThat(result.message).isNull()

                val result2 = categoryRepository.getCategoryById(categoryId)

                assertThat(result2.data).isNotNull()
                assertThat(result2.message).isNull()

                category = result2.data
            }
        }

        return category
    }

    private fun getOrCreateProduct(
        productId: String = "1111",
        productName: String = "New Product",
        productPrice: Int = 100
    ): Product? {
        var product: Product? = null
        val newCategory = getOrCreateCategory()
        Thread.sleep(5000)

        runTest {
            delay(5000)
            try {
                val getProduct = productRepository.getProductById(productId)

                Timber.d("result ${getProduct.data}")
                Timber.d("result ${getProduct.message}")

                assertThat(getProduct.data).isNotNull()
                assertThat(getProduct.message).isNull()

                product = getProduct.data

            }catch (e: AssertionError) {
                Timber.d("category ${category?.categoryId}")

                val newProduct = Product(
                    productId = productId,
                    category = newCategory,
                    productName = productName,
                    productPrice = productPrice,
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = productRepository.createNewProduct(newProduct)
                Timber.d("created ${result.data}")
                Timber.d("created ${result.message}")
                assertThat(result.data).isTrue()

                val getProduct = productRepository.getProductById(productId)
                assertThat(getProduct.data).isNotNull()
                assertThat(getProduct.message).isNull()

                product = getProduct.data
            }
        }

        return product
    }

    private fun getOrCreateCartOrder(
        cartOrderId: String = "1111",
        orderId: String = "1",
        orderType: String = CartOrderType.DineIn.orderType,
        orderStatus: String = OrderStatus.Processing.orderStatus,
    ): CartOrder? {
        var cartOrder: CartOrder? = null

        runTest {
            try {
                val getOrder = cartOrderRepository.getCartOrderById(cartOrderId)
                assertThat(getOrder.data).isNotNull()

                cartOrder = getOrder.data

            }catch (e: AssertionError) {
                val newOrder = CartOrder(
                    cartOrderId = cartOrderId,
                    orderId = orderId,
                    orderType = orderType,
                    cartOrderStatus = orderStatus,
                )

                val result = cartOrderRepository.createNewOrder(newOrder)
                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()

                val getOrder = cartOrderRepository.getCartOrderById(cartOrderId)
                assertThat(getOrder.data).isNotNull()

                cartOrder = getOrder.data
            }
        }

        return cartOrder
    }

    private fun getOrCreateAddress(addressId: String = "1111", addressName: String = "New Address"): Address? {
        var address: Address? = null

        runTest {
            try {
                val result = addressRepository.getAddressById(addressId)
                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                address = result.data
            }catch (e: AssertionError) {
                val newAddress = Address(
                    addressId = addressId,
                    addressName = addressName,
                    shortName = "NA"
                )

                val result = addressRepository.addNewAddress(newAddress)

                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()
                assertThat(result.data).isTrue()

                val result1 = addressRepository.getAddressById(addressId)
                assertThat(result1.data).isNotNull()
                assertThat(result1.message).isNull()

                address = result1.data
            }
        }

        return address
    }

    private fun getOrCreateCustomer(customerId: String = "1111", customerPhone: String = "New Customer"): Customer? {
        var customer: Customer? = null

        runTest {
            try {
                val result = customerRepository.getCustomerById(customerId)
                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                customer = result.data
            }catch (e: AssertionError) {
                val newCustomer = Customer(
                    customerId = customerId,
                    customerPhone = customerPhone,
                )

                val result1 = customerRepository.createNewCustomer(newCustomer)
                assertThat(result1.data).isTrue()
                assertThat(result1.message).isNull()

                val result = customerRepository.getCustomerById(customerId)
                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                customer = result.data
            }
        }

        return customer
    }
}