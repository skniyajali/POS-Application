package com.niyaj.popos.features.address.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.common.util.Resource
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
class AddressRepositoryImplTest {
    private lateinit var repository: AddressRepositoryImpl

    private val newAddress = Address(
        addressId = "1111",
        shortName = "NA",
        addressName = "New Address",
        createdAt = System.currentTimeMillis().toString()
    )

    private val updatedAddress = Address(
        addressId = "1111",
        shortName = "UA",
        addressName = "Updated Address",
        createdAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = AddressRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_address_with_invalid_data_should_fail() {
        runTest {
            val result = repository.addNewAddress(Address("", "s", "cd"))

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to create address")
        }
    }

    @Test
    fun b_create_new_address_with_valid_data_should_pass() {
        runTest {
            val result = repository.addNewAddress(newAddress)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun c_create_new_address_with_valid_data_but_already_exists_should_fail() {
        runTest {
            val result2 = repository.addNewAddress(newAddress)

            assertThat(result2.data).isNotNull()
            assertThat(result2.data).isFalse()
            assertThat(result2.message).isNotNull()
            assertThat(result2.message).isEqualTo("Unable to create address")
        }
    }

    @Test
    fun d_update_address_with_invalid_data_should_fail() {
        runTest {
            val result = repository.updateAddress(Address("", "s", "cd"), "")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to update address")
        }
    }

    @Test
    fun e_update_address_with_valid_data_but_invalid_id_should_fail() {
        runTest {
            val result = repository.updateAddress(updatedAddress, "7899")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to find address")
        }
    }

    @Test
    fun f_update_address_with_valid_data_should_pass() {
        runTest {
            val result2 = repository.updateAddress(updatedAddress, newAddress.addressId)

            assertThat(result2.data).isNotNull()
            assertThat(result2.data).isTrue()
            assertThat(result2.message).isNull()
        }
    }

    @Test
    fun g_get_address_with_invalid_id_should_return_null() {
        runTest {
            val result = repository.getAddressById("0989")

            assertThat(result.data).isNull()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun h_get_address_with_valid_id_should_return_addon_item() {
        runTest {
            val result2 = repository.getAddressById(updatedAddress.addressId)

            assertThat(result2.data).isNotNull()
            assertThat(result2.data?.addressId).isEqualTo(updatedAddress.addressId)
            assertThat(result2.data?.addressName).isEqualTo(updatedAddress.addressName)
            assertThat(result2.data?.shortName).isEqualTo(updatedAddress.shortName)
            assertThat(result2.message).isNull()
        }
    }

    @Test
    fun i_find_address_by_name_return_false() {
        runTest {
            val result = repository.findAddressByName("popos")

            assertThat(result).isFalse()
        }
    }

    @Test
    fun j_find_address_by_name_that_already_exists_return_true() {
        runTest {
            val result2 = repository.findAddressByName(updatedAddress.addressName)

            assertThat(result2).isTrue()
        }
    }

    @Test
    fun k_find_address_by_name_that_already_exists_with_id_return_false() {
        runTest {
            val result2 = repository.findAddressByName(newAddress.addressName, newAddress.addressId)

            assertThat(result2).isFalse()
        }
    }

    @Test
    fun l_validate_address_name_with_empty_data_return_false() {
        runTest {
            val result = repository.validateAddressName("")

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo("Address name must not be empty")
        }
    }

    @Test
    fun m_validate_address_name_with_invalid_data_return_false() {
        runTest {
            val result = repository.validateAddressName("s")

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo("The address name must be more than 2 characters long")
        }
    }

    @Test
    fun n_validate_address_name_with_that_already_exist_return_false() {
        runTest {
            val result = repository.validateAddressName(updatedAddress.addressName)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo("Address name already exists.")
        }
    }

    @Test
    fun o_validate_address_name_with_that_already_exist_with_id_return_true() {
        runTest {
            val result = repository.validateAddressName(updatedAddress.addressName, updatedAddress.addressId)

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    @Test
    fun p_validate_address_name_with_valid_data_return_true() {
        runTest {
            val result = repository.validateAddressName("new address")

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    @Test
    fun q_delete_address_with_invalid_id_return_false() {
        runTest {
            val result = repository.deleteAddress("09876bnd")

            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun r_delete_address_with_valid_id_return_true() {
        runTest {
            val result2 = repository.deleteAddress(newAddress.addressId)

            assertThat(result2).isNotNull()
            assertThat(result2.data).isTrue()
            assertThat(result2.message).isNull()
        }
    }

    @Test
    fun s_getAllAddresses() {
        val data = createNewAddresses()
        assertThat(data).isTrue()

        runTest {
            repository.getAllAddress().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        assertThat(result.data).isNotNull()
                        assertThat(result.message).isNull()
                        assertThat(result.data).isNotEmpty()
                        assertThat(result.data?.size).isEqualTo(10)
                    }
                    else -> {}
                }

            }
        }
    }
    
    private fun createNewAddresses(): Boolean {
        return try {
            runTest {
                val addresses = mutableListOf<Address>()

                ('A'..'J').forEachIndexed { index, c ->
                    addresses.add(
                        Address(
                            addressId = index.toString().plus("index"),
                            addressName = c.toString().plus("complex"),
                            shortName = c.toString().plus("A"),
                            createdAt = System.currentTimeMillis().plus(index).toString()
                        )
                    )
                }

                addresses.shuffle()

                addresses.forEach {newAddress ->
                    val result = repository.addNewAddress(newAddress)

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
}