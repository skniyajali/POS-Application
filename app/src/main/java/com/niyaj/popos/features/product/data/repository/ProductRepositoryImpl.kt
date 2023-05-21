package com.niyaj.popos.features.product.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import com.niyaj.popos.features.product.presentation.details.ProductOrder
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ProductRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository, ProductValidationRepository {
    val realm = Realm.open(config)

    init {
        Timber.d("Product Session")
    }

    override suspend fun getAllProducts(): Flow<Resource<List<Product>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val products = realm.query<Product>().asFlow()

                    products.collect { changes: ResultsChange<Product> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get all products", emptyList()))
                }
            }
        }
    }

    override fun getProductById(productId: String): Resource<Product?> {
        return try {
            val product = realm.query<Product>("productId == $0", productId).first().find()
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get product", null)
        }
    }

    override fun findProductByName(productName: String, productId: String?): Boolean {
        val product = if (productId == null) {
            realm.query<Product>("productName == $0", productName).first().find()
        } else {
            realm.query<Product>("productId != $0 && productName == $1", productId, productName)
                .first().find()
        }

        return product != null
    }

    override suspend fun createNewProduct(newProduct: Product): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateProductName = validateProductName(newProduct.productName, newProduct.productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(validateProductPrice, validateProductName).any { !it.successful }

                if (!hasError) {

                    val category = realm.query<Category>("categoryId == $0", newProduct.category?.categoryId).first().find()

                    if (category != null) {
                        val product = Product()
                        product.productId = newProduct.productId.ifEmpty { BsonObjectId().toHexString() }
                        product.productName = newProduct.productName
                        product.productAvailability = newProduct.productAvailability
                        product.productPrice = newProduct.productPrice
                        product.createdAt = newProduct.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                        realm.write {
                            findLatest(category)?.let { product.category = it }

                            this.copyToRealm(product)
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find category", false)
                    }

                }else {
                    Resource.Error("Unable to validate product", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new product", false)
        }
    }

    override suspend fun updateProduct(newProduct: Product, productId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateProductName = validateProductName(newProduct.productName, productId)
                val validateProductPrice = validateProductPrice(newProduct.productPrice)

                val hasError = listOf(validateProductPrice, validateProductName).any { !it.successful }

                if (!hasError) {
                    val category = realm.query<Category>("categoryId == $0", newProduct.category?.categoryId)
                        .first().find()

                    if (category != null) {
                        val product = realm.query<Product>("productId == $0", productId).first().find()

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
                        }else {
                            Resource.Error("Unable to find product", false)
                        }
                    } else {
                        Resource.Error("Unable to find product category", false)
                    }
                }else {
                    Resource.Error("Unable to validate product", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update product", false)
        }
    }

    override suspend fun deleteProduct(productId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val product = realm.query<Product>("productId == $0", productId).first().find()
                if (product != null) {
                    realm.write {
                        val cartOrders = this.query<CartRealm>("product.productId == $0", productId).find()

                        delete(cartOrders)
                        findLatest(product)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to delete product", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete product", false)
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
                                    this.query<Product>("productId == $0", productId).first().find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.plus(price)
                                }
                            }
                        } else {
                            val products = this.query<Product>().find()

                            products.forEach { product ->
                                product.productPrice = product.productPrice.plus(price)
                            }
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Price must be greater than 0", false)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to increase price", false)
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
                                    this.query<Product>("productId == $0", productId).first().find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.minus(price)
                                }
                            }
                        } else {
                            val products = this.query<Product>().find()

                            products.forEach { product ->
                                product.productPrice = product.productPrice.minus(price)
                            }
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Price must be greater than 0", false)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to increase price", false)
        }
    }

    override suspend fun importProducts(products: List<Product>): Resource<Boolean> {
        return try {
            if (products.isNotEmpty()) {
                withContext(ioDispatcher) {
                    realm.write {
                        products.forEach { product ->
                            val findProduct = this.query<Product>(
                                "productId == $0 OR productName == $1 AND productPrice == $2",
                                product.productId,
                                product.productName,
                                product.productPrice
                            ).first().find()

                            if (findProduct == null) {
                                val newProduct = Product()
                                newProduct.productId = product.productId.ifEmpty { BsonObjectId().toHexString() }
                                newProduct.productName = product.productName
                                newProduct.productPrice = product.productPrice
                                newProduct.productAvailability = product.productAvailability
                                newProduct.createdAt = product.createdAt.ifEmpty { System.currentTimeMillis().toString() }
                                newProduct.updatedAt = System.currentTimeMillis().toString()

                                if (product.category != null){
                                    val category = this.query<Category>(
                                        "categoryId == $0",
                                        product.category?.categoryId
                                    ).first().find()

                                    if (category != null) {
                                        newProduct.category = category
                                    } else {
                                        val newCategory = this.copyToRealm(Category().apply {
                                            categoryId = product.category!!.categoryId
                                            categoryName = product.category!!.categoryName
                                            categoryAvailability = product.category!!.categoryAvailability
                                            createdAt = product.category!!.createdAt.ifEmpty { System.currentTimeMillis().toString() }
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
                Resource.Error("Product list is empty", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import products", false)
        }
    }

    override fun validateCategoryName(categoryName: String): ValidationResult {
        if(categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product Category required",
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateProductName(productName: String, productId: String?): ValidationResult {
        if(productName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name required",
            )
        }

        if(productName.length < 4) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name must be more than 4 characters long"
            )
        }

        val serverResult = findProductByName(productName, productId)

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = "Product name already exists.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateProductPrice(productPrice: Int, type: String?): ValidationResult {
        if(productPrice == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product price required.",
            )
        }

        if(type.isNullOrEmpty() && productPrice < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product price must be at least 10 rupees."
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun getProductOrders(productId : String) : Flow<Resource<List<ProductOrder>>> {
        return channelFlow {
            try {
                withContext(ioDispatcher) {
                    val orders = realm.query<CartRealm>("product.productId == $0", productId)
                        .sort("updatedAt", Sort.DESCENDING).asFlow()

                    orders.collectLatest { result ->
                        when(result) {
                            is InitialResults -> {
                                send(Resource.Success(mapCartOrdersToProductOrders(result.list)))
                                send(Resource.Loading(false))
                            }
                            is UpdatedResults -> {
                                send(Resource.Success(mapCartOrdersToProductOrders(result.list)))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get product orders."))
            }
        }
    }

    private fun getCartOrderDetails(cartOrderId: String): CartOrder {
        return realm.query<CartOrder>("cartOrderId == $0", cartOrderId).find().first()
    }

    private fun mapCartOrdersToProductOrders(orders: List<CartRealm>): List<ProductOrder> {
        val groupedOrder = orders.groupBy { it.cartOrder?.cartOrderId }

        return groupedOrder.map { results ->
            if (results.key != null) {
                val cartOrder = getCartOrderDetails(results.key!!)
                val totalQuantity = results.value.sumOf { it.quantity }

                ProductOrder(
                    cartOrderId = cartOrder.cartOrderId,
                    orderId = cartOrder.orderId,
                    orderedDate = cartOrder.updatedAt ?: cartOrder.createdAt,
                    orderType = cartOrder.orderType,
                    quantity = totalQuantity,
                    customerPhone = cartOrder.customer?.customerPhone,
                    customerAddress = cartOrder.address?.addressName
                )
            }else {
                ProductOrder()
            }
        }
    }
}