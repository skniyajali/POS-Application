package com.niyaj.popos.features.product.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ProductRepositoryImpl(
    config: RealmConfiguration
) : ProductRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Product Session")
    }

    override suspend fun getAllProducts(): Flow<Resource<List<Product>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<Product>().sort("productId", Sort.DESCENDING).asFlow()

                items.collect { changes: ResultsChange<Product> ->
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

    override suspend fun getProductById(productId: String): Resource<Product?> {
        return try {
            val product = realm.query<Product>("productId == $0", productId).first().find()
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get product", null)
        }
    }

    override suspend fun getProductsByCategoryId(categoryId: String): Resource<Boolean> {
        return try {
            realm.query<Product>("category.categoryId == $0", categoryId).find()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get products", false)
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
            val category =
                realm.query<Category>("categoryId == $0", newProduct.category?.categoryId).first()
                    .find()

            if (category != null) {
                val product = Product()
                product.productId = BsonObjectId().toHexString()
                product.productName = newProduct.productName
                product.productAvailability = newProduct.productAvailability
                product.productPrice = newProduct.productPrice
                product.createdAt = System.currentTimeMillis().toString()

                realm.write {

                    findLatest(category)?.also { product.category = it }

                    this.copyToRealm(product)
                }

                Resource.Success(true)

            } else {
                Resource.Error("Unable to find product category", false)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new product", false)
        }
    }

    override suspend fun updateProduct(newProduct: Product, id: String): Resource<Boolean> {
        return try {
            val category =
                realm.query<Category>("categoryId == $0", newProduct.category?.categoryId).first()
                    .find()

            if (category != null) {
                realm.write {
                    val product = this.query<Product>("productId == $0", id).first().find()
                    product?.productName = newProduct.productName
                    product?.productAvailability = newProduct.productAvailability
                    product?.productPrice = newProduct.productPrice
                    product?.updatedAt = System.currentTimeMillis().toString()

                    findLatest(category)?.also { product?.category = it }
                }
                Resource.Success(true)
            } else {
                Resource.Error("Unable to find product category", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update product", false)
        }
    }

    override suspend fun deleteProduct(productId: String): Resource<Boolean> {
        return try {
            realm.write {
                val product = this.query<Product>("productId == $0", productId).first().find()
                val cartOrders = this.query<CartRealm>("product.productId == $0", productId).find()
                if (product != null) {
                    delete(cartOrders)
                    delete(product)
                } else {
                    Resource.Error("Unable to delete product", false)
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete product", false)
        }
    }

    override suspend fun increasePrice(
        price: Int,
        productList: List<String>,
    ): Resource<Boolean> {
        return try {
            if (price >= 0) {
                CoroutineScope(Dispatchers.IO).launch {
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

    override suspend fun decreasePrice(
        price: Int,
        productList: List<String>,
    ): Resource<Boolean> {
        return try {
            if (price >= 0) {
                CoroutineScope(Dispatchers.IO).launch {
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
                            newProduct.productId = product.productId
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

                                    val newCategory = this.copyToRealm(instance = Category().apply {
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

                Resource.Success(true)
            } else {
                Resource.Error("Product list is empty", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import products", false)
        }
    }

    override suspend fun exportProducts(limit: Int?): Resource<List<Product>> {
        return Resource.Success(emptyList())
    }
}