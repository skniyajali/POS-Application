package com.niyaj.popos.features.expenses_category.data.repository

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
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
class ExpensesCategoryRepositoryImplTest {

    private lateinit var repository: ExpensesCategoryRepositoryImpl

    private val newExpensesCategory = ExpensesCategory(
        expensesCategoryId = "4444",
        expensesCategoryName = "New Expenses",
        createdAt = System.currentTimeMillis().toString(),
    )

    private val updatedExpensesCategory = ExpensesCategory(
        expensesCategoryId = "4444",
        expensesCategoryName = "Updated Expenses",
        createdAt = System.currentTimeMillis().toString(),
    )


    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = ExpensesCategoryRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_expenses_category_with_empty_data_return_false() = runTest {
        val result = repository.createNewExpensesCategory(ExpensesCategory())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate expenses category")
    }

    @Test
    fun b_create_new_expenses_category_with_valid_data_return_true() = runTest {
        val result = repository.createNewExpensesCategory(newExpensesCategory)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_expenses_with_invalid_id_return_false() = runTest {
        val result = repository.getExpensesCategoryById("90jd")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun d_get_expenses_with_valid_id_return_true() = runTest {
        val result = repository.getExpensesCategoryById(newExpensesCategory.expensesCategoryId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.expensesCategoryId).isEqualTo(newExpensesCategory.expensesCategoryId)
        assertThat(result.data?.expensesCategoryName).isEqualTo(newExpensesCategory.expensesCategoryName)
    }

    @Test
    fun e_validate_expenses_name_with_empty_data_return_false() = runTest {
        val result = repository.validateExpensesCategoryName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Category name is empty")
    }

    @Test
    fun f_validate_expenses_name_with_invalid_data_return_false() = runTest {
        val result = repository.validateExpensesCategoryName("dj")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Invalid category name")
    }

    @Test
    fun g_validate_expenses_name_with_invalid_data_with_digit_return_false() = runTest {
        val result = repository.validateExpensesCategoryName("djd4")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Category name must not contain any digit")
    }

    @Test
    fun h_validate_expenses_name_with_valid_data_return_true() = runTest {
        val result = repository.validateExpensesCategoryName(newExpensesCategory.expensesCategoryName)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun i_update_expenses_category_with_empty_data_return_false() = runTest {
        val result = repository.updateExpensesCategory(ExpensesCategory(),"")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate expenses category")
    }

    @Test
    fun j_update_expenses_category_with_valid_data_and_invalid_id_return_false() = runTest {
        val result = repository.updateExpensesCategory(updatedExpensesCategory,"89dj")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find expense category")
    }

    @Test
    fun k_update_expenses_category_with_valid_data_and_valid_id_return_true() = runTest {
        val result = repository.updateExpensesCategory(updatedExpensesCategory,newExpensesCategory.expensesCategoryId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getExpensesCategoryById(updatedExpensesCategory.expensesCategoryId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()
        assertThat(result2.data?.expensesCategoryId).isEqualTo(updatedExpensesCategory.expensesCategoryId)
        assertThat(result2.data?.expensesCategoryName).isEqualTo(updatedExpensesCategory.expensesCategoryName)
    }

    @Test
    fun l_delete_expenses_category_with_invalid_id_return_false() = runTest {
        val result = repository.deleteExpensesCategory("89d")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find expense category")
    }

    @Test
    fun m_delete_expenses_category_with_valid_id_return_true() = runTest {
        val result = repository.deleteExpensesCategory(updatedExpensesCategory.expensesCategoryId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getExpensesCategoryById(updatedExpensesCategory.expensesCategoryId)

        assertThat(result2.data).isNull()
        assertThat(result2.message).isNull()
    }

    @Test
    fun n_get_all_expenses_category() = runTest {

        createNewExpensesCategory()

        repository.getAllExpensesCategory().onEach {resource ->
            when(resource) {
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

    private fun createNewExpensesCategory(): Boolean {
        return try {
            val categories = mutableListOf<ExpensesCategory>()

            ('A'..'E').forEachIndexed { index, c ->
                categories.add(
                    ExpensesCategory(
                        expensesCategoryId = index.plus(8912).toString(),
                        expensesCategoryName = c.plus("category"),
                        createdAt = System.currentTimeMillis().toString(),
                    )
                )
            }

            runTest {
                categories.forEach { category ->
                    val result = repository.createNewExpensesCategory(category)

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