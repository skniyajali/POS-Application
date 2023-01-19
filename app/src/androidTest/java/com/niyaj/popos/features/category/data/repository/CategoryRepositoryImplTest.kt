package com.niyaj.popos.features.category.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.category.domain.model.Category
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
class CategoryRepositoryImplTest {

    private val config = TestConfig.config()

    private lateinit var repository: CategoryRepositoryImpl

   private val newCategory = Category(
       categoryId = "1111",
       categoryName = "New Category",
       categoryAvailability = true,
       createdAt = System.currentTimeMillis().toString()
   )

    private val updatedCategory = Category(
        categoryId = "1111",
        categoryName = "Updated Category",
        categoryAvailability = true,
    )

    @Before
    fun setUp() = runTest {
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = CategoryRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_validate_category_name_with_empty_data_should_fail() {
        val result = repository.validateCategoryName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Category name must not be empty")
    }

    @Test
    fun b_validate_category_name_with_invalid_data_should_fail() {
        val result = repository.validateCategoryName("ss")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Category name must be 3 characters long")
    }

    @Test
    fun c_validate_category_name_with_valid_data_should_pass() {
        val result = repository.validateCategoryName("new category")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun d_create_new_category_with_invalid_data_return_false () {
        runTest {
            val result = repository.createNewCategory(Category("", "sf"))

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun e_create_new_category_with_valid_data_return_true () {
        runTest {
            testScheduler.advanceUntilIdle()

            val result = repository.createNewCategory(newCategory)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun f_get_category_with_invalid_id_return_null() {
        runTest {
            val result = repository.getCategoryById("skjf")

            assertThat(result.data).isNull()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun h_get_category_with_valid_id_return_category() {
        runTest {
            val result = repository.getCategoryById(newCategory.categoryId)

            assertThat(result.data).isNotNull()
            assertThat(result.data?.categoryId).isEqualTo(newCategory.categoryId)
            assertThat(result.data?.categoryName).isEqualTo(newCategory.categoryName)
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun i_find_category_by_new_name_return_false() {
        runTest {
            val result = repository.findCategoryByName("new_name", null)

            assertThat(result).isFalse()
        }
    }

    @Test
    fun j_find_category_by_name_and_id_return_false() {
        runTest {
            val result = repository.findCategoryByName(newCategory.categoryName, newCategory.categoryId)

            assertThat(result).isFalse()
        }
    }

    @Test
    fun k_find_category_by_name_and_without_id_return_true() {
        runTest {
            val result = repository.findCategoryByName(newCategory.categoryName, categoryId = null)

            assertThat(result).isTrue()
        }
    }

    @Test
    fun l_update_category_with_invalid_data_without_id_return_false() {
        runTest {
            val result = repository.updateCategory(Category("", "kj"), "")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun m_update_category_with_valid_data_with_invalid_id_return_false() {
        runTest {
            val result = repository.updateCategory(updatedCategory, "90s")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
        }
    }

    @Test
    fun n_update_category_with_valid_data_return_true() {
        runTest {
            val result = repository.updateCategory(updatedCategory, newCategory.categoryId)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun o_delete_data_with_invalid_id_return_false() {
        runTest {
            val result = repository.deleteCategory("908dk")

            assertThat(result.data).isNotNull()
            assertThat(result.data).isFalse()
            assertThat(result.message).isNotNull()
            assertThat(result.message).isEqualTo("Unable to find category")
        }
    }

    @Test
    fun p_delete_data_with_valid_id_return_true() {
        runTest {
            val result = repository.deleteCategory(updatedCategory.categoryId)

            assertThat(result.data).isNotNull()
            assertThat(result.data).isTrue()
            assertThat(result.message).isNull()
        }
    }

    @Test
    fun q_get_all_categories() {

        createNewCategories()

        runTest {
            repository.getAllCategories().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        assertThat(result.data).isNotNull()
                        assertThat(result.data).isNotEmpty()
                        assertThat(result.message).isEmpty()
                        assertThat(result.data?.size).isEqualTo(10)
                    }
                   else -> {}
                }
            }
        }
    }

    private fun createNewCategory(): Category? {
        return try {
            runTest {
                val result = repository.createNewCategory(newCategory)

                assertThat(result.data).isNotNull()
                assertThat(result.data).isTrue()
                assertThat(result.message).isNull()
            }

            return newCategory
        }catch (e: AssertionError) {
            null
        }
    }

    private fun createNewCategories(): Boolean {
        return try {
            val categories = mutableListOf<Category>()

            ('A'..'J').forEachIndexed { index, c ->
                categories.add(
                    Category(
                        categoryId = index.toString(),
                        categoryName = c.toString().plus("category"),
                        categoryAvailability = true,
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }

            categories.shuffle()

            runTest {
                categories.forEach { category ->
                    val result = repository.createNewCategory(category)

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