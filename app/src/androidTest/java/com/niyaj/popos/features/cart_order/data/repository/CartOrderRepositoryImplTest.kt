package com.niyaj.popos.features.cart_order.data.repository

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.popos.features.customer.domain.model.Customer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CartOrderRepositoryImplTest {

    private lateinit var customerRepository : CustomerRepositoryImpl
    private lateinit var addressRepository: AddressRepositoryImpl
    private lateinit var addOnItemRepository: AddOnItemRepositoryImpl
    private lateinit var repository: CartOrderRepositoryImpl
    private lateinit var settingsRepository: SettingsRepository

    private var customer: Customer? = null
    private var addOnItem: AddOnItem? = null
    private var address: Address? = null

    private val newCartOrder = CartOrder(
        cartOrderId = "1111",
        orderId = "1",
        orderType = CartOrderType.DineIn.orderType,
        cartOrderStatus = OrderStatus.Processing.orderStatus,
    )

    private var updatedCartOrder: CartOrder = CartOrder()

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        settingsRepository = SettingsRepositoryImpl(config, dispatcher)
        addressRepository = AddressRepositoryImpl(config, dispatcher)
        customerRepository = CustomerRepositoryImpl(config, dispatcher)
        addOnItemRepository = AddOnItemRepositoryImpl(config, dispatcher)
        repository = CartOrderRepositoryImpl(config, settingsRepository, this, dispatcher)

        addOnItem = getOrCreateAddOnItem()
        address = getOrCreateAddress()
        customer = getOrCreateCustomer()

        updatedCartOrder = CartOrder(
            cartOrderId = "1111",
            orderId = "1",
            orderType = CartOrderType.DineOut.orderType,
            address = address,
            customer = customer,
            cartOrderStatus = OrderStatus.Processing.orderStatus,
        )
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_cart_order_with_empty_data_return_false() = runTest {
        val result = repository.createNewOrder(CartOrder())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate cart order")
    }

    @Test
    fun b_create_new_cart_order_with_valid_data_return_true() = runTest {
        val result = repository.createNewOrder(newCartOrder)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_cart_order_with_invalid_id_return_false() = runTest {
        val result = repository.getCartOrderById("89hd")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun d_get_cart_order_with_valid_id_return_cart_order() = runTest {
        val result = repository.getCartOrderById(newCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.orderId).isEqualTo(newCartOrder.orderId)
        assertThat(result.data?.cartOrderId).isEqualTo(newCartOrder.cartOrderId)
        assertThat(result.data?.orderType).isEqualTo(newCartOrder.orderType)
        assertThat(result.data?.cartOrderStatus).isEqualTo(newCartOrder.cartOrderStatus)
    }

    @Test
    fun e_check_does_recently_created_order_added_in_selected_cart_order() = runTest {
        repository.getSelectedCartOrders().onEach {data ->
            assertThat(data).isNotNull()
            assertThat(data?.cartOrder?.orderId).isEqualTo(newCartOrder.orderId)
            assertThat(data?.cartOrder?.cartOrderId).isEqualTo(newCartOrder.cartOrderId)
        }
    }

    @Test
    fun f_update_cart_order_with_empty_data_return_false() = runTest{
        val result = repository.updateCartOrder(CartOrder(), "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate cart order")
    }

    @Test
    fun g_update_cart_order_with_valid_data_and_invalid_id_return_false() = runTest{
        val result = repository.updateCartOrder(updatedCartOrder, "098")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find cart order")
    }

    @Test
    fun h_update_cart_order_with_valid_data_and_valid_id_return_true() = runTest{
        val result = repository.updateCartOrder(updatedCartOrder, newCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getCartOrderById(updatedCartOrder.cartOrderId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.orderId).isEqualTo(updatedCartOrder.orderId)
        assertThat(result2.data?.cartOrderId).isEqualTo(updatedCartOrder.cartOrderId)
        assertThat(result2.data?.orderType).isEqualTo(updatedCartOrder.orderType)
        assertThat(result2.data?.cartOrderStatus).isEqualTo(updatedCartOrder.cartOrderStatus)
    }

    @Test
    fun i_add_addon_item_with_invalid_addon_id_return_false() = runTest {
        val result = repository.updateAddOnItem("89ff", "90878")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find add-on item")
    }

    @Test
    fun j_add_addon_item_with_valid_addon_id_and_invalid_cart_order_id_return_false() = runTest {
        val result = repository.updateAddOnItem("1111", "90878")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find cart order")
    }

    @Test
    fun k_add_addon_item_with_valid_addon_id_and_valid_cart_order_id_return_true() = runTest {
        val result = repository.updateAddOnItem("1111", updatedCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getCartOrderById(updatedCartOrder.cartOrderId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.cartOrderId).isEqualTo(updatedCartOrder.cartOrderId)
        assertThat(result2.data?.addOnItems).isNotNull()
        assertThat(result2.data?.addOnItems).isNotEmpty()
        assertThat(result2.data?.addOnItems?.first()?.addOnItemId).isNotNull()
        assertThat(result2.data?.addOnItems?.first()?.addOnItemId).isEqualTo(addOnItem?.addOnItemId)
        assertThat(result2.data?.addOnItems?.first()?.itemName).isEqualTo(addOnItem?.itemName)
    }

    @Test
    fun l_remove_addon_item_with_valid_addon_id_and_valid_cart_order_id_return_true() = runTest {
        val result = repository.updateAddOnItem("1111", updatedCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getCartOrderById(updatedCartOrder.cartOrderId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.cartOrderId).isEqualTo(updatedCartOrder.cartOrderId)
        assertThat(result2.data?.addOnItems).isEmpty()
    }

    @Test
    fun m_place_order_with_invalid_id_return_false() = runTest {
        val result = repository.placeOrder("898d")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find cart order")
    }

    @Test
    fun n_place_order_with_valid_id_return_true() = runTest {
        val result = repository.placeOrder(updatedCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getCartOrderById(updatedCartOrder.cartOrderId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.orderId).isEqualTo(updatedCartOrder.orderId)
        assertThat(result2.data?.cartOrderId).isEqualTo(updatedCartOrder.cartOrderId)
        assertThat(result2.data?.orderType).isEqualTo(updatedCartOrder.orderType)
        assertThat(result2.data?.cartOrderStatus).isEqualTo(OrderStatus.Placed.orderStatus)
        assertThat(result2.data?.updatedAt).isNotNull()
    }

    @Test
    fun o_delete_selected_cart_order_return_true() = runTest {
        val result = repository.deleteSelectedCartOrder()

        assertThat(result).isTrue()

        repository.getSelectedCartOrders().onEach {data ->
            assertThat(data).isNull()
        }
    }

    @Test
    fun p_add_selected_cart_order_with_invalid_cart_order_id_return_false() = runTest {
        val result = repository.addSelectedCartOrder("907889")

        assertThat(result).isFalse()
    }

    @Test
    fun q_add_selected_cart_order_with_valid_cart_order_id_return_true() = runTest {
        val result = repository.addSelectedCartOrder(updatedCartOrder.cartOrderId)

        assertThat(result).isTrue()

        repository.getSelectedCartOrders().onEach {data ->
            assertThat(data).isNotNull()
            assertThat(data?.cartOrder?.orderId).isEqualTo(updatedCartOrder.orderId)
            assertThat(data?.cartOrder?.cartOrderId).isEqualTo(updatedCartOrder.cartOrderId)
            assertThat(data?.cartOrder?.cartOrderStatus).isEqualTo(updatedCartOrder.cartOrderStatus)
            assertThat(data?.cartOrder?.orderType).isEqualTo(updatedCartOrder.orderType)
        }
    }

    @Test
    fun r_validate_cart_order_id_with_empty_data_return_false() = runTest {
        val result = repository.validateOrderId("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The order id must not be empty")
    }

    @Test
    fun s_validate_cart_order_id_with_valid_data_return_true() = runTest {
        val result = repository.validateOrderId(newCartOrder.orderId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun t_validate_address_with_empty_data_for_dine_in_order_return_true() = runTest {
        val result = repository.validateCustomerAddress(CartOrderType.DineIn.orderType, "")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun u_validate_address_with_empty_data_for_dine_out_order_return_false() = runTest {
        val result = repository.validateCustomerAddress(CartOrderType.DineOut.orderType, "")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Customer address must not be empty")
    }

    @Test
    fun v_validate_address_with_invalid_data_for_dine_out_order_return_false() = runTest {
        val result = repository.validateCustomerAddress(CartOrderType.DineOut.orderType, "h")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The address must be more than 2 characters long")
    }

    @Test
    fun w_validate_address_with_valid_data_for_dine_out_order_return_true() = runTest {
        val result = repository.validateCustomerAddress(CartOrderType.DineOut.orderType, "New Address")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun x_validate_phone_with_empty_data_for_dine_in_order_return_true() = runTest {
        val result = repository.validateCustomerPhone(CartOrderType.DineIn.orderType, "")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun y_validate_phone_with_empty_data_for_dine_out_order_return_false() = runTest {
        val result = repository.validateCustomerPhone(CartOrderType.DineOut.orderType, "")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no must not be empty")
    }

    @Test
    fun z0_validate_phone_with_invalid_data_for_dine_out_order_return_false() = runTest {
        val result = repository.validateCustomerPhone(CartOrderType.DineOut.orderType, "999999")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no must be 10 digits long")
    }

    @Test
    fun z1_validate_phone_with_invalid_data_with_letter_for_dine_out_order_return_false() = runTest {
        val result = repository.validateCustomerPhone(CartOrderType.DineOut.orderType, "999999999v")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no does not contains any characters")
    }

    @Test
    fun z2_validate_phone_with_valid_data_for_dine_out_order_return_true() = runTest {
        val result = repository.validateCustomerPhone(CartOrderType.DineOut.orderType, "9078563412")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun z3_delete_cart_order_with_invalid_id_return_false() = runTest {
        val result = repository.deleteCartOrder("90769")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find cart order")
    }

    @Test
    fun z4_delete_cart_order_with_valid_id_return_true() = runTest {
        val result = repository.deleteCartOrder(updatedCartOrder.cartOrderId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        repository.getSelectedCartOrders().onEach {data ->
            assertThat(data).isNull()
        }
    }

    @Test
    fun z5_get_all_cart_orders() = runTest {
        val result = createNewCartOrders()
        assertThat(result).isTrue()

        repository.getAllCartOrders().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.data?.size).isEqualTo(5)
                    assertThat(resource.message).isNull()
                }
                else -> {}
            }
        }
    }

    @Test
    fun z6_place_all_order_return_true() = runTest {
        val cartOrders = mutableListOf<String>()

        repository.getAllCartOrders().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.data?.size).isEqualTo(5)
                    assertThat(resource.message).isNull()

                    resource.data?.let { orders ->
                        val data = orders.map { it.cartOrderId }
                        cartOrders.addAll(data)
                    }
                }
                else -> {}
            }
        }

        cartOrders.forEach { id ->
            val result2 = repository.getCartOrderById(id)

            assertThat(result2.data).isNotNull()
            assertThat(result2.message).isNull()
            assertThat(result2.data?.cartOrderStatus).isEqualTo(OrderStatus.Placed.orderStatus)
            assertThat(result2.data?.updatedAt).isNotNull()
        }
    }

    @Test
    fun z7_delete_old_cart_orders_return_true() = runTest {
        val result = repository.deleteCartOrders(false)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()

        repository.getAllCartOrders().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }
    }

    @Test
    fun z8_delete_all_cart_orders_return_true() = runTest {
        val result = repository.deleteCartOrders(true)
        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()

        repository.getAllCartOrders().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isEmpty()
                    assertThat(resource.message).isNull()
                }
                else -> {}
            }
        }

        repository.getSelectedCartOrders().onEach {data ->
            assertThat(data).isNull()
        }
    }

    private fun getOrCreateAddress(addressId: String =  "1111", addressName: String = "New Address"): Address? {
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
                    shortName = "NA",
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = addressRepository.addNewAddress(newAddress)

                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()
                assertThat(result.message).isNull()

                val result2 = addressRepository.getAddressById(addressId)

                assertThat(result2.data).isNotNull()
                assertThat(result2.message).isNull()

                address = result2.data
            }
        }

        return address
    }

    private fun getOrCreateCustomer(customerId: String = "1111", customerPhone: String =  "9078563421"): Customer? {
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
                    customerName = "New Customer",
                    customerEmail = "new@gmail.com",
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = customerRepository.createNewCustomer(newCustomer)

                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()
                assertThat(result.message).isNull()

                val result2 = customerRepository.getCustomerById(customerId)

                assertThat(result2.data).isNotNull()
                assertThat(result2.message).isNull()

                customer = result2.data
            }
        }

        return customer
    }

    private fun getOrCreateAddOnItem(addOnItemId: String = "1111", addOnName: String = "New AddOn"): AddOnItem? {
        var addOnItem: AddOnItem? = null
        runTest {
            try {
                val result = addOnItemRepository.getAddOnItemById(addOnItemId)
                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                addOnItem = result.data
            }catch (e: AssertionError) {
                val newAddOnItem = AddOnItem(
                    addOnItemId = addOnItemId,
                    itemName = addOnName,
                    itemPrice = 10,
                    createdAt = System.currentTimeMillis().toString()
                )

                val result = addOnItemRepository.createNewAddOnItem(newAddOnItem)
                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()

                val result2 = addOnItemRepository.getAddOnItemById(addOnItemId)
                assertThat(result2.data).isNotNull()
                assertThat(result2.message).isNull()

                addOnItem = result2.data
            }
        }

        return addOnItem
    }

    private fun createNewCartOrders(): Boolean {
        return try {
            val cartOrders = mutableListOf<CartOrder>()

            ('A'..'E').forEachIndexed { index, _ ->
                cartOrders.add(
                    CartOrder(
                        cartOrderId = index.plus(4440).toString(),
                        orderId = index.toString(),
                    )
                )
            }
            runTest {
                cartOrders.forEach { cartOrder ->
                    val result = repository.createNewOrder(cartOrder)

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                    assertThat(result.message).isNull()

                    repository.getSelectedCartOrders().onEach { data ->
                        assertThat(data).isNotNull()
                        assertThat(data?.cartOrder?.orderId).isEqualTo(cartOrder.orderId)
                        assertThat(data?.cartOrder?.cartOrderId).isEqualTo(cartOrder.cartOrderId)
                        assertThat(data?.cartOrder?.cartOrderStatus).isEqualTo(cartOrder.cartOrderStatus)
                        assertThat(data?.cartOrder?.orderType).isEqualTo(cartOrder.orderType)
                    }
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }
}