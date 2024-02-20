package com.niyaj.data.data.repository

import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_LENGTH_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Category
import com.niyaj.model.OrderType
import com.niyaj.model.Product
import com.niyaj.model.ProductOrder
import com.niyaj.model.filterByCategory
import com.niyaj.model.filterProducts
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class ProductRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : ProductRepository, ProductValidationRepository {
    
    val realm = Realm.open(config)

    override suspend fun getAllCategories(): Flow<List<Category>> {
        return withContext(ioDispatcher) {
            realm.query<CategoryEntity>()
                .sort("categoryId", Sort.ASCENDING)
                .find()
                .asFlow()
                .mapLatest { items ->
                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it }
                    )
                }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Category? {
        return try {
            withContext(ioDispatcher) {
                realm.query<CategoryEntity>("categoryId == $0", categoryId).first().find()
                    ?.toExternalModel()
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllProducts(
        searchText: String,
        categoryId: String
    ): Flow<List<Product>> {
        return withContext(ioDispatcher) {
            realm.query<ProductEntity>()
                .sort("productId", Sort.ASCENDING)
                .asFlow()
                .mapLatest { products ->
                    products.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = {
                            it.filterByCategory(categoryId).filterProducts(searchText)
                        },
                    )
                }
        }
    }

    override suspend fun getProductById(productId: String): Resource<Product?> {
        return try {
            val product = realm.query<ProductEntity>("productId == $0", productId).first().find()
            Resource.Success(product?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get product")
        }
    }

    override suspend fun findProductByName(productName: String, productId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (productId == null) {
                realm.query<ProductEntity>("productName == $0", productName).first().find()
            } else {
                realm.query<ProductEntity>(
                    "productId != $0 && productName == $1",
                    productId,
                    productName
                ).first().find()
            } != null
        }
    }

    override suspend fun createOrUpdateProduct(
        newProduct: Product,
        productId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateProductName = validateProductName(newProduct.productName, productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError =
                    listOf(validateProductPrice, validateProductName).any { !it.successful }

                if (!hasError) {
                    val category = realm.query<CategoryEntity>(
                        "categoryId == $0",
                        newProduct.category?.categoryId
                    ).first().find()

                    if (category != null) {
                        val product =
                            realm.query<ProductEntity>("productId == $0", productId).first().find()

                        if (product != null) {
                            realm.write {
                                findLatest(product)?.apply {
                                    this.productName = newProduct.productName
                                    this.productAvailability = newProduct.productAvailability
                                    this.productPrice = newProduct.productPrice
                                    this.updatedAt = System.currentTimeMillis().toString()

                                    findLatest(category)?.also { this.category = it }
                                }
                            }

                            Resource.Success(true)
                        } else {
                            realm.write {
                                this.copyToRealm(newProduct.toEntity(findLatest(category)))
                            }

                            Resource.Success(true)
                        }
                    } else {
                        Resource.Error("Unable to find product category")
                    }
                } else {
                    Resource.Error("Unable to validate product")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update product")
        }
    }

    override suspend fun deleteProducts(productIds: List<String>): Resource<Boolean> {
        return try {
            productIds.forEach { productId ->
                withContext(ioDispatcher) {
                    val product =
                        realm.query<ProductEntity>("productId == $0", productId).first().find()
                    if (product != null) {
                        realm.write {
                            val cartOrders =
                                this.query<CartEntity>("product.productId == $0", productId).find()

                            delete(cartOrders)
                            findLatest(product)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete product")
        }
    }

    override suspend fun increasePrice(price: Int, productList: List<String>): Resource<Boolean> {
        return try {
            if (price > 0) {
                withContext(ioDispatcher) {
                    realm.write {
                        if (productList.isNotEmpty()) {
                            productList.forEach { productId ->
                                val product =
                                    this.query<ProductEntity>("productId == $0", productId).first()
                                        .find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.plus(price)
                                }
                            }
                        } else {
                            val products = this.query<ProductEntity>().find()

                            products.forEach { product ->
                                product.productPrice = product.productPrice.plus(price)
                            }
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Price must be greater than 0")
            }
        } catch (e: Exception) {
            Resource.Error("Unable to increase price")
        }
    }

    override suspend fun decreasePrice(price: Int, productList: List<String>): Resource<Boolean> {
        return try {
            if (price > 0) {
                withContext(ioDispatcher) {
                    realm.write {
                        if (productList.isNotEmpty()) {
                            productList.forEach { productId ->
                                val product =
                                    this.query<ProductEntity>("productId == $0", productId).first()
                                        .find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.minus(price)
                                }
                            }
                        } else {
                            val products = this.query<ProductEntity>().find()

                            products.forEach { product ->
                                product.productPrice = product.productPrice.minus(price)
                            }
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Price must be greater than 0")
            }
        } catch (e: Exception) {
            Resource.Error("Unable to increase price")
        }
    }

    override suspend fun importProducts(products: List<Product>): Resource<Boolean> {
        return try {
            if (products.isNotEmpty()) {
                withContext(ioDispatcher) {
                    realm.write {
                        products.forEach { product ->
                            val findProduct = this.query<ProductEntity>(
                                "productId == $0 OR productName == $1 AND productPrice == $2",
                                product.productId,
                                product.productName,
                                product.productPrice
                            ).first().find()

                            if (findProduct == null) {
                                val newProduct = ProductEntity()
                                newProduct.productId =
                                    product.productId.ifEmpty { BsonObjectId().toHexString() }
                                newProduct.productName = product.productName
                                newProduct.productPrice = product.productPrice
                                newProduct.productAvailability = product.productAvailability
                                newProduct.createdAt = product.createdAt.ifEmpty {
                                    System.currentTimeMillis().toString()
                                }
                                newProduct.updatedAt = System.currentTimeMillis().toString()

                                if (product.category != null) {
                                    val category = this.query<CategoryEntity>(
                                        "categoryId == $0",
                                        product.category?.categoryId
                                    ).first().find()

                                    if (category != null) {
                                        newProduct.category = category
                                    } else {
                                        val newCategory = this.copyToRealm(CategoryEntity().apply {
                                            categoryId = product.category!!.categoryId
                                            categoryName = product.category!!.categoryName
                                            categoryAvailability =
                                                product.category!!.categoryAvailability
                                            createdAt = product.category!!.createdAt.ifEmpty {
                                                System.currentTimeMillis().toString()
                                            }
                                            updatedAt = System.currentTimeMillis().toString()
                                        }, updatePolicy = UpdatePolicy.ALL)

                                        newProduct.category = newCategory
                                    }
                                }

                                this.copyToRealm(newProduct)
                            }
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Product list is empty")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import products")
        }
    }

    override suspend fun getProductOrders(productId: String): Flow<List<ProductOrder>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    val orders = realm.query<CartEntity>("product.productId == $0", productId)
                        .sort("updatedAt", Sort.DESCENDING).asFlow()

                    orders.collectLatest { result ->
                        when (result) {
                            is InitialResults -> {
                                send(mapCartOrdersToProductOrders(result.list))
                            }

                            is UpdatedResults -> {
                                send(mapCartOrdersToProductOrders(result.list))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    private fun getCartOrderDetails(cartOrderId: String): CartOrderEntity {
        return realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).find().first()
    }

    private fun mapCartOrdersToProductOrders(orders: List<CartEntity>): List<ProductOrder> {
        val groupedOrder = orders.groupBy { it.cartOrder?.cartOrderId }

        return groupedOrder.map { results ->
            if (results.key != null) {
                val cartOrder = getCartOrderDetails(results.key!!)
                val totalQuantity = results.value.sumOf { it.quantity }

                ProductOrder(
                    cartOrderId = cartOrder.cartOrderId,
                    orderId = cartOrder.orderId,
                    orderedDate = cartOrder.updatedAt ?: cartOrder.createdAt,
                    orderType = OrderType.valueOf(cartOrder.orderType),
                    quantity = totalQuantity,
                    customerPhone = cartOrder.customer?.customerPhone,
                    customerAddress = cartOrder.address?.addressName
                )
            } else {
                ProductOrder()
            }
        }
    }

    override fun validateCategoryName(categoryName: String): ValidationResult {
        if (categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_CATEGORY_EMPTY_ERROR,
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateProductName(
        productName: String,
        productId: String?
    ): ValidationResult {
        if (productName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_EMPTY_ERROR,
            )
        }

        if (productName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            findProductByName(productName, productId)
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateProductPrice(productPrice: Int): ValidationResult {
        if (productPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_EMPTY_ERROR,
            )
        }

        if (productPrice < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_PRICE_LENGTH_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}