package com.niyaj.popos.features.product.data.repository

import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
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
class ProductRepositoryImplTest {

    private lateinit var repository: ProductRepositoryImpl
    private lateinit var categoryRepository: CategoryRepositoryImpl
    private var category: Category? = null

    private var newProduct = Product()
    private var updatedProduct = Product()
    private var updatedProductWithCategory = Product()

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        categoryRepository = CategoryRepositoryImpl(config, dispatcher)
        repository = ProductRepositoryImpl(config, dispatcher)
        category = getOrCreateCategory("1111", "New Category")

        newProduct = Product(
            productId = "1111",
            category = category,
            productName = "New Product",
            productPrice = 80,
            createdAt = System.currentTimeMillis().toString()
        )

        updatedProduct = Product(
            productId = "1111",
            category = category,
            productName = "Updated Product",
            productPrice = 130,
            updatedAt = System.currentTimeMillis().toString()
        )

        updatedProductWithCategory = Product(
            productId = "1111",
            category = getOrCreateCategory("2222", "Updated Category"),
            productName = "Updated Product",
            productPrice = 130,
            updatedAt = System.currentTimeMillis().toString()
        )

    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }

    @Test
    fun a_create_new_product_with_empty_data_return_false() = runTest {
        val result = repository.createNewProduct(Product())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate product")
    }

    @Test
    fun b_create_new_product_with_valid_data_and_invalid_category_return_false() = runTest {
        val result = repository.createNewProduct(Product("89739", null, "New Product", 20))

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find category")
    }

    @Test
    fun c_create_new_product_with_valid_data_and_valid_category_return_true() = runTest {
        val result = repository.createNewProduct(newProduct)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun d_get_product_by_id_with_invalid_id_return_null() = runTest {
        val result = repository.getProductById("89jk")

        assertThat(result.data).isNull()
        assertThat(result.message).isNull()
    }

    @Test
    fun e_get_product_by_id_with_valid_id_return_product() = runTest {
        val result = repository.getProductById(newProduct.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()

        assertThat(result.data?.productId).isEqualTo(newProduct.productId)
        assertThat(result.data?.productName).isEqualTo(newProduct.productName)
        assertThat(result.data?.productPrice).isEqualTo(newProduct.productPrice)
        assertThat(result.data?.category?.categoryId).isEqualTo(newProduct.category?.categoryId)
        assertThat(result.data?.category?.categoryName).isEqualTo(newProduct.category?.categoryName)
    }

    @Test
    fun f_validate_product_name_with_empty_data_return_false() = runTest {
        val result = repository.validateProductName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Product name required")
    }

    @Test
    fun g_validate_product_name_with_invalid_data_return_false() = runTest {
        val result = repository.validateProductName("dk")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Product name must be more than 4 characters long")
    }

    @Test
    fun h_validate_product_name_with_that_already_exists_return_false() = runTest {
        val result = repository.validateProductName(newProduct.productName)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Product name already exists.")
    }

    @Test
    fun i_validate_product_name_with_valid_data_return_true() = runTest {
        val result = repository.validateProductName("testing product")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun j_validate_product_price_with_empty_data_return_false() = runTest {
        val result = repository.validateProductPrice(0)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Product price required.")
    }

    @Test
    fun k_validate_product_price_with_invalid_data_return_false() = runTest {
        val result = repository.validateProductPrice(5)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Product price must be at least 10 rupees.")
    }

    @Test
    fun l_validate_product_price_with_valid_data_return_true() = runTest {
        val result = repository.validateProductPrice(12)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun m_find_product_by_name_that_already_exist_return_true() = runTest {
        val result = repository.findProductByName(newProduct.productName)

        assertThat(result).isTrue()
    }

    @Test
    fun n_find_product_by_name_that_already_exist_with_id_return_false() = runTest {
        val result = repository.findProductByName(newProduct.productName, newProduct.productId)

        assertThat(result).isFalse()
    }

    @Test
    fun o_find_product_by_name_with_new_name_return_false() = runTest {
        val result = repository.findProductByName("Testing Product")

        assertThat(result).isFalse()
    }

    @Test
    fun p_update_product_by_with_empty_data_return_false() = runTest {
        val result = repository.updateProduct(Product(), "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate product")
    }

    @Test
    fun q_update_product_by_with_valid_data_and_invalid_category_return_false() = runTest {
        val result = repository.updateProduct(Product("89739", null, "Product", 20), "")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find product category")
    }

    @Test
    fun r_update_product_by_with_valid_data_and_invalid_id_return_false() = runTest {
        val result = repository.updateProduct(updatedProduct, "9098d")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to find product")
    }

    @Test
    fun s_update_product_by_with_valid_data_and_valid_id_return_true() = runTest {
        val result = repository.updateProduct(updatedProduct, newProduct.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getProductById(updatedProduct.productId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()

        assertThat(result2.data?.productId).isEqualTo(updatedProduct.productId)
        assertThat(result2.data?.productName).isEqualTo(updatedProduct.productName)
        assertThat(result2.data?.productPrice).isEqualTo(updatedProduct.productPrice)
        assertThat(result2.data?.category?.categoryId).isEqualTo(updatedProduct.category?.categoryId)
        assertThat(result2.data?.category?.categoryName).isEqualTo(updatedProduct.category?.categoryName)
    }

    @Test
    fun t_update_product_by_with_valid_data_with_different_category_and_valid_id_return_true() = runTest {
        val result = repository.updateProduct(updatedProductWithCategory, updatedProduct.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()

        val result2 = repository.getProductById(updatedProduct.productId)

        assertThat(result2.data).isNotNull()
        assertThat(result2.message).isNull()

        assertThat(result2.data?.productId).isEqualTo(updatedProductWithCategory.productId)
        assertThat(result2.data?.productName).isEqualTo(updatedProductWithCategory.productName)
        assertThat(result2.data?.productPrice).isEqualTo(updatedProductWithCategory.productPrice)
        assertThat(result2.data?.category?.categoryId).isEqualTo(updatedProductWithCategory.category?.categoryId)
        assertThat(result2.data?.category?.categoryName).isEqualTo(updatedProductWithCategory.category?.categoryName)
    }

    @Test
    fun u_increase_price_with_invalid_price_return_false() = runTest {
        val result = repository.increasePrice(0, listOf())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Price must be greater than 0")
    }

    @Test
    fun v_increase_price_with_valid_price_return_false() = runTest {
        val result2 = repository.increasePrice(10, listOf(newProduct.productId))

        assertThat(result2.data).isNotNull()
        assertThat(result2.data).isTrue()
        assertThat(result2.message).isNull()

        val result = repository.getProductById(newProduct.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()

        assertThat(result.data?.productId).isEqualTo(updatedProductWithCategory.productId)
        assertThat(result.data?.productName).isEqualTo(updatedProductWithCategory.productName)
        assertThat(result.data?.productPrice).isEqualTo(updatedProductWithCategory.productPrice.plus(10))
        assertThat(result.data?.category?.categoryId).isEqualTo(updatedProductWithCategory.category?.categoryId)
        assertThat(result.data?.category?.categoryName).isEqualTo(updatedProductWithCategory.category?.categoryName)

    }

    @Test
    fun w_decrease_price_with_invalid_price_return_false() = runTest {
        val result = repository.decreasePrice(0, listOf())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Price must be greater than 0")
    }

    @Test
    fun x_decrease_price_with_valid_price_return_false() = runTest {
        val result2 = repository.decreasePrice(10, listOf(newProduct.productId))

        assertThat(result2.data).isNotNull()
        assertThat(result2.data).isTrue()
        assertThat(result2.message).isNull()

        val result = repository.getProductById(newProduct.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()

        assertThat(result.data?.productId).isEqualTo(updatedProductWithCategory.productId)
        assertThat(result.data?.productName).isEqualTo(updatedProductWithCategory.productName)
        assertThat(result.data?.productPrice).isEqualTo(updatedProductWithCategory.productPrice)
        assertThat(result.data?.category?.categoryId).isEqualTo(updatedProductWithCategory.category?.categoryId)
        assertThat(result.data?.category?.categoryName).isEqualTo(updatedProductWithCategory.category?.categoryName)

    }

    @Test
    fun y_delete_product_with_invalid_id_return_false() = runTest {
        val result = repository.deleteProduct("9079")

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to delete product")
    }

    @Test
    fun z0_delete_product_with_valid_id_return_true() = runTest {
        val result = repository.deleteProduct(updatedProductWithCategory.productId)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun z1_get_all_products_return_products() = runTest {
        val data = createProducts()
        assertThat(data).isTrue()

        repository.getAllProducts().onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    assertThat(resource.data).isNotNull()
                    assertThat(resource.data).isNotEmpty()
                    assertThat(resource.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }
    }


    private fun getOrCreateCategory(categoryId: String, categoryName: String): Category? {
        val newCategory = Category(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryAvailability = true,
            createdAt = System.currentTimeMillis().toString()
        )

        var category: Category? = null

        runTest {
            try {
                val result = categoryRepository.getCategoryById(categoryId)

                assertThat(result.data).isNotNull()
                assertThat(result.message).isNull()

                category = result.data
            }catch (e: AssertionError) {
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

    private fun createProducts(): Boolean {
        return try {
            val products = mutableListOf<Product>()

            ('A'..'E').forEachIndexed { index, c ->
                products.add(
                    Product(
                        productId = index.plus(4440).toString(),
                        productName = c.plus("product"),
                        productPrice = index.plus(100),
                        category = category,
                        createdAt = System.currentTimeMillis().toString(),
                    )
                )
            }

            runTest {
                products.forEach { product ->
                    val result = repository.createNewProduct(product)

                    assertThat(result.data).isNotNull()
                    assertThat(result.message).isNull()
                    assertThat(result.data).isTrue()
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }
}