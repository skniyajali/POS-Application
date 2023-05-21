package com.niyaj.popos.features.customer.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class CustomerRepositoryImplTest {

    private lateinit var repository: CustomerRepositoryImpl

    private val newCustomer = Customer(
        customerId = "1111",
        customerPhone = "9078563421",
        customerName = "New Customer",
        customerEmail = "niyaj@gmail.com",
        createdAt = System.currentTimeMillis().toString(),
    )

    private val updatedCustomer = Customer(
        customerId = "1111",
        customerPhone = "8078563421",
        customerName = "Updated Customer",
        customerEmail = "updated@gmail.com",
        updatedAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = CustomerRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_customer_with_invalid_data_return_false() = runTest {
        val result = repository.createNewCustomer(Customer())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate customer")
    }

    @Test
    fun b_create_new_customer_with_valid_data_return_true() = runTest {
        val result = repository.createNewCustomer(newCustomer)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_customer_with_invalid_id_return_null() = runTest {
        val result = repository.getCustomerById("90ss")

        assertThat(result.data).isNull()
    }

    @Test
    fun d_get_customer_with_valid_id_return_customer() = runTest {
        val result = repository.getCustomerById(newCustomer.customerId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.customerId).isEqualTo(newCustomer.customerId)
        assertThat(result.data?.customerName).isEqualTo(newCustomer.customerName)
        assertThat(result.data?.customerEmail).isEqualTo(newCustomer.customerEmail)
        assertThat(result.data?.customerPhone).isEqualTo(newCustomer.customerPhone)
    }

    @Test
    fun e_validate_customer_name_with_empty_data_return_true() = runTest {
        val result = repository.validateCustomerName()

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun f_validate_customer_name_with_invalid_data_return_false() = runTest {
        val result = repository.validateCustomerName("nd")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Customer name must be 3 characters long")
    }

    @Test
    fun g_validate_customer_name_with_valid_data_return_true() = runTest {
        val result = repository.validateCustomerName("customer")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun h_validate_customer_email_with_empty_data_return_true() = runTest {
        val result = repository.validateCustomerEmail()

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun i_validate_customer_email_with_invalid_data_return_false() = runTest {
        val result = repository.validateCustomerEmail("ndj")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Customer email is not a valid email address.")
    }

    @Test
    fun j_validate_customer_email_with_valid_data_return_true() = runTest {
        val result = repository.validateCustomerEmail("new@gmail.com")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun k_validate_customer_phone_with_empty_data_return_false() = runTest {
        val result = repository.validateCustomerPhone("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no must not be empty")
    }

    @Test
    fun l_validate_customer_phone_with_less_data_return_false() = runTest {
        val result = repository.validateCustomerPhone("78983")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no must be 10 digits")
    }

    @Test
    fun m_validate_customer_phone_with_data_and_digit_return_false() = runTest {
        val result = repository.validateCustomerPhone("789838933c")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no should not contains any characters")
    }

    @Test
    fun n_validate_customer_phone_with_data_that_already_exist_return_false() = runTest {
        val result = repository.validateCustomerPhone(newCustomer.customerPhone)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("The phone no already exists")
    }

    @Test
    fun o_validate_customer_phone_with_data_that_already_exist_with_id_return_true() = runTest {
        val result = repository.validateCustomerPhone(newCustomer.customerPhone, customerId = newCustomer.customerId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun p_validate_customer_phone_with_valid_data_return_true() = runTest {
        val result = repository.validateCustomerPhone("7856341290")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun q_update_customer_with_invalid_data_return_false() = runTest {
        val repository = repository.updateCustomer(Customer(), "")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to validate customer")
    }

    @Test
    fun r_update_customer_with_valid_data_and_invalid_id_return_false() = runTest {
        val repository = repository.updateCustomer(updatedCustomer, "89dhjd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find customer")
    }

    @Test
    fun s_update_customer_with_valid_data_return_true() = runTest {
        val resource = repository.updateCustomer(updatedCustomer, newCustomer.customerId)

        assertThat(resource.data).isNotNull()
        assertThat(resource.data).isTrue()
        assertThat(resource.message).isNull()

        val result = repository.getCustomerById(updatedCustomer.customerId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.customerId).isEqualTo(updatedCustomer.customerId)
        assertThat(result.data?.customerName).isEqualTo(updatedCustomer.customerName)
        assertThat(result.data?.customerEmail).isEqualTo(updatedCustomer.customerEmail)
        assertThat(result.data?.customerPhone).isEqualTo(updatedCustomer.customerPhone)
    }

    @Test
    fun t_delete_customer_with_invalid_id_return_false() = runTest {
        val repository = repository.deleteCustomer("09sd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find customer")
    }

    @Test
    fun u_delete_customer_with_valid_id_return_true() = runTest {
        val repository = repository.deleteCustomer(updatedCustomer.customerId)

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isTrue()
        assertThat(repository.message).isNull()
    }

    @Test
    fun v_get_all_customers_return_customers() = runTest {
        val data = createNewCustomers()
        assertThat(data).isTrue()

        repository.getAllCustomers().onEach {result ->
            when (result) {
                is Resource.Success -> {
                    assertThat(result.data).isNotNull()
                    assertThat(result.message).isNull()
                    assertThat(result.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }

    }

    @Test
    fun w_delete_all_customers_return_true() = runTest {
        val result = repository.deleteAllCustomer()

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        repository.getAllCustomers().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data).isEmpty()
                }
                else -> {}
            }
        }
    }

    @Test
    fun x_import_customers_return_true() = runTest {
        val customers = getImportedCustomers()

        val result = repository.importContacts(customers)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        repository.getAllCustomers().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }

        val result2 = repository.importContacts(customers)

        assertThat(result2.data).isNotNull()
        assertThat(result2.data).isTrue()
        assertThat(result2.message).isNull()

        repository.getAllCustomers().onEach {resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.message).isNull()
                    assertThat(resource.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }

    }

    private fun createNewCustomers(): Boolean {
        return try {
            val customers = mutableListOf<Customer>()

            ('A'..'E').forEachIndexed { index, c ->
                customers.add(
                    Customer(
                        customerId = index.toString().plus("id"),
                        customerEmail = c.plus("@gmail.com"),
                        customerName = c.plus("name"),
                        customerPhone = index.plus(9999999000).toString(),
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }
            runTest {
                customers.forEach { customer ->
                    val result = repository.createNewCustomer(customer)

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

    private fun getImportedCustomers(): List<Customer> {
        val customers = mutableListOf<Customer>()

        ('A'..'E').forEachIndexed { index, c ->
            customers.add(
                Customer(
                    customerId = index.toString().plus("id"),
                    customerEmail = c.plus("@gmail.com"),
                    customerName = c.plus("name"),
                    customerPhone = index.plus(9999999000).toString(),
                    createdAt = System.currentTimeMillis().toString()
                )
            )
        }

        return customers
    }
}