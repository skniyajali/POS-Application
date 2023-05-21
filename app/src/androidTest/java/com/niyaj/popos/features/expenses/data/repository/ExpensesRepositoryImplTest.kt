package com.niyaj.popos.features.expenses.data.repository

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.data.repository.ExpensesCategoryRepositoryImpl
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.utils.getEndTime
import com.niyaj.popos.utils.getStartTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ExpensesRepositoryImplTest {

    private lateinit var repository: ExpensesRepositoryImpl
    private lateinit var expensesCategory: ExpensesCategoryRepositoryImpl
    private lateinit var settingsRepository: SettingsRepository
    private var category: ExpensesCategory? = null
    private var updatedCategory: ExpensesCategory? = null
    private var newExpenses: Expenses = Expenses()
    private var updatedExpenses = Expenses()
    private var updatedExpensesWithCategory = Expenses()

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        expensesCategory = ExpensesCategoryRepositoryImpl(config, dispatcher)

        settingsRepository = SettingsRepositoryImpl(config, dispatcher)

        repository = ExpensesRepositoryImpl(config, settingsRepository, dispatcher)

        category = createAndGetExpensesCategory("1111", "New Category")
        updatedCategory = createAndGetExpensesCategory("2222", "Updated Category")

        newExpenses = Expenses(
            expensesId = "1111",
            expensesCategory = category,
            expensesPrice = "4000",
            expensesRemarks = "New Expenses",
            createdAt = System.currentTimeMillis().toString()
        )

        updatedExpenses = Expenses(
            expensesId = "1111",
            expensesCategory = category,
            expensesPrice = "6000",
            expensesRemarks = "Updated Expenses",
            updatedAt = System.currentTimeMillis().toString()
        )

        updatedExpensesWithCategory = Expenses(
            expensesId = "1111",
            expensesCategory = updatedCategory,
            expensesPrice = "8000",
            expensesRemarks = "Updated Expenses Category",
            updatedAt = System.currentTimeMillis().toString()
        )
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_expenses_with_empty_data_return_false() = runTest {
        val result = repository.createNewExpenses(Expenses())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate expenses")
    }

    @Test
    fun b_create_new_expenses_without_category_data_return_false() = runTest {
        val result = repository.createNewExpenses(Expenses("222", expensesCategory = null, expensesPrice = "890"))

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate expenses")
    }

    @Test
    fun c_create_new_expenses_with_valid_data_return_true() = runTest {
        val result = repository.createNewExpenses(newExpenses)

        Timber.d("result ${result.data}")
        Timber.d("result ${result.message}")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun d_get_expenses_with_invalid_id_return_false() = runTest {
        val result = repository.getExpensesById("09jd")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun e_get_expenses_with_valid_id_return_expenses() = runTest {
        val result = repository.getExpensesById(newExpenses.expensesId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.expensesId).isEqualTo(newExpenses.expensesId)
        assertThat(result.data?.expensesCategory?.expensesCategoryId).isEqualTo(newExpenses.expensesCategory?.expensesCategoryId)
        assertThat(result.data?.expensesCategory?.expensesCategoryName).isEqualTo(newExpenses.expensesCategory?.expensesCategoryName)
        assertThat(result.data?.expensesPrice).isEqualTo(newExpenses.expensesPrice)
        assertThat(result.data?.expensesRemarks).isEqualTo(newExpenses.expensesRemarks)
    }

    @Test
    fun f_validate_expenses_price_with_empty_data_return_false() = runTest {
        val result = repository.validateExpensesPrice("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Expanses price must not be empty")
    }

    @Test
    fun g_validate_expenses_price_with_data_and_letter_return_false() = runTest {
        val result = repository.validateExpensesPrice("788df")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Expanses price must not contain a letter")
    }

    @Test
    fun h_validate_expenses_price_with_higher_price_return_false() = runTest {
        val result = repository.validateExpensesPrice("7889990")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Invalid expanses price.")
    }

    @Test
    fun i_validate_expenses_price_with_valid_price_return_true() = runTest {
        val result = repository.validateExpensesPrice(newExpenses.expensesPrice)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun j_validate_expenses_category_with_empty_data_return_false() = runTest {
        val result = repository.validateExpensesCategory("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Category is required")
    }

    @Test
    fun k_validate_expenses_category_with_invalid_category_id_return_false() = runTest {
        val result = repository.validateExpensesCategory("09jkd")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Unable to find expenses category")
    }

    @Test
    fun l_validate_expenses_category_with_valid_category_id_return_true() = runTest {
        val result = repository.validateExpensesCategory(newExpenses.expensesCategory?.expensesCategoryId ?: "")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun m_update_expenses_with_empty_data_return_false() = runTest {
        val result = repository.updateExpenses(Expenses(), "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate expenses")
    }

    @Test
    fun n_update_expenses_with_valid_data_and_invalid_id_return_false() = runTest {
        val result = repository.updateExpenses(updatedExpenses, "djd")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find expenses")
    }

    @Test
    fun o_update_expenses_with_valid_data_with_same_category_return_true() = runTest {
        val result = repository.updateExpenses(updatedExpenses, newExpenses.expensesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getExpensesById(updatedExpenses.expensesId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.expensesId).isEqualTo(updatedExpenses.expensesId)
        assertThat(result2.data?.expensesCategory?.expensesCategoryId).isEqualTo(updatedExpenses.expensesCategory?.expensesCategoryId)
        assertThat(result2.data?.expensesCategory?.expensesCategoryName).isEqualTo(updatedExpenses.expensesCategory?.expensesCategoryName)
        assertThat(result2.data?.expensesPrice).isEqualTo(updatedExpenses.expensesPrice)
        assertThat(result2.data?.expensesRemarks).isEqualTo(updatedExpenses.expensesRemarks)
    }

    @Test
    fun p_update_expenses_with_valid_data_with_different_category_return_true() = runTest {
        val result = repository.updateExpenses(updatedExpensesWithCategory, updatedExpenses.expensesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getExpensesById(updatedExpenses.expensesId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.expensesId).isEqualTo(updatedExpensesWithCategory.expensesId)
        assertThat(result2.data?.expensesCategory?.expensesCategoryId).isEqualTo(updatedExpensesWithCategory.expensesCategory?.expensesCategoryId)
        assertThat(result2.data?.expensesCategory?.expensesCategoryName).isEqualTo(updatedExpensesWithCategory.expensesCategory?.expensesCategoryName)
        assertThat(result2.data?.expensesPrice).isEqualTo(updatedExpensesWithCategory.expensesPrice)
        assertThat(result2.data?.expensesRemarks).isEqualTo(updatedExpensesWithCategory.expensesRemarks)
    }

    @Test
    fun q_delete_expenses_with_invalid_id_return_false() = runTest {
        val result = repository.deleteExpenses("09e0")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find expense item")
    }

    @Test
    fun r_delete_expenses_with_invalid_id_return_true() = runTest {
        val result = repository.deleteExpenses(updatedExpenses.expensesId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun s_get_all_expenses() = runTest {
        val data = createNewExpenses()
        assertThat(data).isTrue()

        repository.getAllExpenses(getStartTime, getEndTime).onEach { resource ->
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

    private fun createAndGetExpensesCategory(expCatId: String, expCatName: String): ExpensesCategory? {
        val newExpensesCategory = ExpensesCategory(
            expensesCategoryId = expCatId,
            expensesCategoryName = expCatName,
            createdAt = System.currentTimeMillis().toString(),
        )

        var category: ExpensesCategory? = null

        runTest {
            try {
                val data = expensesCategory.getExpensesCategoryById(expCatId)
                assertThat(data.data).isNotNull()
                assertThat(data.message).isNull()

                category = data.data
            }catch (e: AssertionError) {
                val result = expensesCategory.createNewExpensesCategory(newExpensesCategory)
                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()

                val data = expensesCategory.getExpensesCategoryById(expCatId)
                assertThat(data.data).isNotNull()
                assertThat(data.message).isNull()

                category = data.data
            }
        }

        return category
    }

    private fun createNewExpenses(): Boolean {
        return try {
            val category = createAndGetExpensesCategory("1111","New Category")
            val expenses = mutableListOf<Expenses>()

            ('A'..'E').forEachIndexed { index, c ->
                expenses.add(
                    Expenses(
                        expensesId = c.plus(8992).toString(),
                        expensesCategory = category,
                        expensesPrice = index.plus(4000).toString(),
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }

            runTest {
                expenses.forEach { expenses ->
                    val result = repository.createNewExpenses(expenses)

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