package com.niyaj.popos.features.addon_item.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_EMPTY_ERROR
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
class AddOnItemRepositoryImplTest {
    private val configuration =  TestConfig.config()

    private lateinit var repository: AddOnItemRepositoryImpl

    private val newAddOnItem = AddOnItem(
        addOnItemId = "1111",
        itemName = "new addon",
        itemPrice = 10,
        createdAt = System.currentTimeMillis().toString()
    )

    private val updatedItem = AddOnItem(
        addOnItemId = "1111",
        itemName = "updated addon",
        itemPrice = 12,
        createdAt = System.currentTimeMillis().toString()
    )

    @Before
    fun setUp() = runTest {
        val dispatcher = TestConfig.testDispatcher(testScheduler)
        repository = AddOnItemRepositoryImpl(configuration, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_addon_with_empty_data_should_fail() {
        runTest {
            val result = repository.createNewAddOnItem(AddOnItem())

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun b_create_new_addon_with_only_item_name_should_fail() {
        runTest {
            val result = repository.createNewAddOnItem(AddOnItem(itemName = "foo"))

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun c_create_new_addon_with_only_item_price_should_fail() {
        runTest {
            val result = repository.createNewAddOnItem(AddOnItem(itemPrice = 4))

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun d_create_new_add_on_item_should_pass() {
        runTest {
            val result = repository.createNewAddOnItem(newAddOnItem)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()

        }
    }

    @Test
    fun e_update_add_on_item_with_invalid_data_should_fail() {
        runTest {
            val result = repository.updateAddOnItem(AddOnItem(itemName = "fokko", itemPrice = 12), "224")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to find add on item")
        }
    }

    @Test
    fun f_update_add_on_item_should_pass() {
        runTest {
            val result = repository.updateAddOnItem(updatedItem, newAddOnItem.addOnItemId)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun g_get_add_on_item_by_id_should_return_null() {
        runTest {
            val result = repository.getAddOnItemById("23")

            assertThat(result.data).isNull()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun h_get_add_on_item_by_id_should_return_addon_item() {
        runTest {
            val result = repository.getAddOnItemById(updatedItem.addOnItemId)

            assertThat(result.data).isNotNull()
            assertThat(result.data?.itemName).isEqualTo(updatedItem.itemName)
            assertThat(result.data?.itemPrice).isEqualTo(updatedItem.itemPrice)
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun i_find_add_on_item_by_name_without_id_return_true() {
        runTest {
            val result = repository.findAddOnItemByName(updatedItem.itemName, null)

            assertThat(result).isTrue()
        }
    }

    @Test
    fun j_find_add_on_item_by_name_with_id_return_false() {
        runTest {
            val result = repository.findAddOnItemByName(updatedItem.itemName, updatedItem.addOnItemId)

            assertThat(result).isFalse()
        }
    }

    @Test
    fun k_find_add_on_item_by_different_name_return_false() {
        runTest {
            val result = repository.findAddOnItemByName("new", null)

            assertThat(result).isFalse()
        }
    }

    @Test
    fun l_delete_add_on_item_by_invalid_id_return_false() {
        runTest {
            val result = repository.deleteAddOnItem("673")

            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to find add on item")
        }
    }

    @Test
    fun m_delete_add_on_item_by_valid_id_return_true() {
        runTest {
            val result = repository.deleteAddOnItem(updatedItem.addOnItemId)

            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun n_get_all_addon_items() {
        runTest {

            val data = createAddOnItems()

            assertThat(data).isTrue()

            repository.getAllAddOnItems().onEach { result ->
                when (result){
                    is Resource.Success -> {
                        assertThat(result.data).isNotNull()
                        assertThat(result.data).isNotEmpty()

                        result.data?.let {
                            assertThat(it.size).isEqualTo(10)
                        }
                    }
                    else -> { }
                }
            }
        }
    }

    @Test
    fun o_validate_item_name_with_empty_data_return_false() {
        runTest {
            val result = repository.validateItemName("", null)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(ADDON_NAME_EMPTY_ERROR)
        }
    }

    @Test
    fun p_validate_item_name_with_invalid_data_return_false() {
        runTest {
            val result = repository.validateItemName("sjd", null)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(AddOnConstants.ADDON_NAME_LENGTH_ERROR)
        }
    }

    @Test
    fun q_validate_item_name_with_invalid_data_and_digit_return_false() {
        runTest {
            val result = repository.validateItemName("testing4", null)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(AddOnConstants.ADDON_NAME_DIGIT_ERROR)
        }
    }

    @Test
    fun r_validate_item_name_that_already_exist_with_id_return_true() {
        runTest {
            val result = repository.validateItemName("Aitem", "0222e")

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    @Test
    fun s_validate_item_name_that_already_exist_return_false() {
        runTest {
            val result = repository.validateItemName("Aitem", null)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR)
        }
    }

    @Test
    fun t_validate_item_name_with_whitelist_data_and_digit_return_true() {
        runTest {
            val result = repository.validateItemName("Cold4", null)

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    @Test
    fun u_validate_item_name_with_valid_data_return_true() {
        runTest {
            val result = repository.validateItemName("Testing", null)

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    @Test
    fun v_validate_item_price_with_empty_data_returns_false() {
        runTest {
            val result = repository.validateItemPrice(0)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(AddOnConstants.ADDON_PRICE_EMPTY_ERROR)
        }
    }

    @Test
    fun w_validate_item_price_with_invalid_data_returns_false() {
        runTest {
            val result = repository.validateItemPrice(4)

            assertThat(result.successful).isFalse()
            assertThat(result.errorMessage).isNotNull()
            assertThat(result.errorMessage).isEqualTo(AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR)
        }
    }

    @Test
    fun x_validate_item_price_with_valid_data_returns_true() {
        runTest {
            val result = repository.validateItemPrice(10)

            assertThat(result.successful).isTrue()
            assertThat(result.errorMessage).isNull()
        }
    }

    private fun createAddOnItems(): Boolean {
        return try {
            val addOnItems = mutableListOf<AddOnItem>()

            ('A'..'J').forEachIndexed { index, c ->
                addOnItems.add(
                    AddOnItem(
                        addOnItemId = index.toString().plus("222e"),
                        itemName = c.toString().plus("item"),
                        itemPrice = index.plus(5),
                        createdAt = System.currentTimeMillis().plus(index).toString()
                    )
                )
            }

            addOnItems.shuffle()

            runTest {
                addOnItems.forEach {
                    val result = repository.createNewAddOnItem(it)

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }
    
}