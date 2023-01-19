package com.niyaj.popos.features.charges.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.charges.domain.model.Charges
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
class ChargesRepositoryImplTest {

    private lateinit var repository: ChargesRepositoryImpl

    private val newCharges = Charges(
        chargesId = "1111",
        chargesName = "New Charges",
        chargesPrice = 10,
        isApplicable = true,
        createdAt = System.currentTimeMillis().toString()
    )

    private val updatedCharges = Charges(
        chargesId = "1111",
        chargesName = "Updated Charges",
        chargesPrice = 12,
        isApplicable = true,
        updatedAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = ChargesRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }


    @Test
    fun a_create_new_charges_with_empty_data_return_false() = runTest {
        val result = repository.createNewCharges(Charges())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
    }

    @Test
    fun b_create_new_charges_with_invalid_data_return_false() = runTest {
        val result = repository.createNewCharges(Charges("", "sjd", 7))

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
    }

    @Test
    fun c_create_new_charges_with_valid_data_return_true() = runTest {
        val result = repository.createNewCharges(newCharges)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_charges_with_invalid_id_return_false() = runTest {
        val result = repository.getChargesById("098dh")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun d_get_charges_with_valid_id_return_charges() = runTest {
        val result = repository.getChargesById(newCharges.chargesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data?.chargesId).isEqualTo(newCharges.chargesId)
        assertThat(result.data?.chargesName).isEqualTo(newCharges.chargesName)
        assertThat(result.data?.chargesPrice).isEqualTo(newCharges.chargesPrice)
        assertThat(result.message).isNull()
    }

    @Test
    fun e_update_charges_with_invalid_data_return_false() = runTest {
        val result = repository.updateCharges(Charges("", "sjd", 7), "98j")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to valid charges item")
    }

    @Test
    fun f_update_charges_with_valid_data_with_invalid_id_return_false() = runTest {
        val result = repository.updateCharges(updatedCharges, "98j")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find charges item")
    }

    @Test
    fun g_update_charges_with_valid_data_with_valid_id_return_true() = runTest {
        val result = repository.updateCharges(updatedCharges, newCharges.chargesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun h_validate_charges_name_with_empty_data_return_false() = runTest {
        val result = repository.validateChargesName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges Name must not be empty")
    }

    @Test
    fun i_validate_charges_name_with_invalid_data_return_false() = runTest {
        val result = repository.validateChargesName("dkdj")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges Name must be more than 5 characters long")
    }

    @Test
    fun j_validate_charges_name_with_invalid_data_with_digit_return_false() = runTest {
        val result = repository.validateChargesName("dkdjd4")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges Name must not contain a digit")
    }

    @Test
    fun k_validate_charges_name_with_valid_data_that_already_exists_return_false() = runTest {
        val result = repository.validateChargesName(updatedCharges.chargesName)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges Name already exists.")
    }

    @Test
    fun l_validate_charges_name_with_valid_data_that_already_exists_with_id_return_true() = runTest {
        val result = repository.validateChargesName(updatedCharges.chargesName, updatedCharges.chargesId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun m_validate_charges_name_with_valid_data_return_true() = runTest {
        val result = repository.validateChargesName(newCharges.chargesName)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun n_validate_charges_price_with_empty_data_return_false() = runTest {
        val result = repository.validateChargesPrice(updatedCharges.isApplicable, 0)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges price required.")
    }

    @Test
    fun o_validate_charges_price_with_invalid_data_return_false() = runTest {
        val result = repository.validateChargesPrice(updatedCharges.isApplicable, 4)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Charges Price must be greater than 10 rupees.")
    }

    @Test
    fun p_validate_charges_price_with_valid_data_return_true() = runTest {
        val result = repository.validateChargesPrice(updatedCharges.isApplicable, updatedCharges.chargesPrice)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun q_find_charges_by_name_with_valid_data_with_id_return_false() = runTest {
        val result = repository.findChargesByName(updatedCharges.chargesName, updatedCharges.chargesId)

        assertThat(result).isFalse()
    }

    @Test
    fun r_find_charges_by_name_with_valid_data_without_id_return_true() = runTest {
        val result = repository.findChargesByName(updatedCharges.chargesName, null)

        assertThat(result).isTrue()
    }

    @Test
    fun s_delete_charges_with_invalid_id_return_false() = runTest {
        val result = repository.deleteCharges("9d8j")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find charges item")
    }

    @Test
    fun t_delete_charges_with_valid_id_return_true() = runTest {
        val result = repository.deleteCharges(updatedCharges.chargesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun u_get_all_charges() {
        createNewCharges()

        runTest {
            repository.getAllCharges().onEach { result ->
                when(result) {
                    is Resource.Success -> {
                        assertThat(result.data).isNotNull()
                        assertThat(result.data).isNotEmpty()
                        assertThat(result.data?.size).isEqualTo(10)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun createNewCharges(): Boolean {
        return try {
            val charges = mutableListOf<Charges>()

            ('A'..'J').filterIndexed { index, c ->
                charges.add(
                    Charges(
                        chargesId = index.toString().plus("index"),
                        chargesName = c.toString().plus("charges"),
                        chargesPrice = index.plus(10),
                        isApplicable = true,
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }

            charges.shuffle()

            runTest {
                charges.forEach { charge ->
                    val result = repository.createNewCharges(charge)

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