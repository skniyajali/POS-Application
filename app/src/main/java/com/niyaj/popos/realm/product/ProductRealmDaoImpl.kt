package com.niyaj.popos.realm.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.category.domain.model.Category
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductRealmDaoImpl(
    config: RealmConfiguration
) : ProductRealmDao {

    val realm = Realm.open(config)

    init {
        Timber.d("Product Session")
    }

    override suspend fun getAllProducts(): Flow<Resource<List<ProductRealm>>> {
        return flow {
            try {
                emit(Resource.Loading(true))

                val items = realm.query<ProductRealm>().sort("_id", Sort.DESCENDING).asFlow()

                items.collect { changes: ResultsChange<ProductRealm> ->
                    when (changes) {
                        is UpdatedResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }

                        is InitialResults -> {
                            emit(Resource.Success(changes.list))
                            emit(Resource.Loading(false))
                        }
                    }
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unable to get all products", emptyList()))
            }
        }
    }

    override suspend fun getProductById(productId: String): Resource<ProductRealm?> {
        return try {
            val product = realm.query<ProductRealm>("_id == $0", productId).first().find()
            Resource.Success(product)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get product", null)
        }
    }

    override suspend fun getProductsByCategoryId(categoryId: String): Resource<Boolean> {
        return try {
            realm.query<ProductRealm>("category._id == $0", categoryId).find()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get products", false)
        }
    }

    override fun findProductByName(productName: String, productId: String?): Boolean {
        val product = if (productId == null) {
            realm.query<ProductRealm>("productName == $0", productName).first().find()
        } else {
            realm.query<ProductRealm>("_id != $0 && productName == $1", productId, productName)
                .first().find()
        }

        return product != null
    }

    override suspend fun createNewProduct(newProduct: Product): Resource<Boolean> {
        return try {
            val category =
                realm.query<Category>("_id == $0", newProduct.category.categoryId).first()
                    .find()

            if (category != null) {
                val product = ProductRealm()
                product.productName = newProduct.productName
                product.productAvailability = newProduct.productAvailability
                product.productPrice = newProduct.productPrice

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
                realm.query<Category>("_id == $0", newProduct.category.categoryId).first()
                    .find()

            if (category != null) {
                realm.write {
                    val product = this.query<ProductRealm>("_id == $0", id).first().find()
                    product?.productName = newProduct.productName
                    product?.productAvailability = newProduct.productAvailability
                    product?.productPrice = newProduct.productPrice
                    product?.updated_at = System.currentTimeMillis().toString()

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
                val product = this.query<ProductRealm>("_id == $0", productId).first().find()
                val cartOrders = this.query<CartRealm>("product._id == $0", productId).find()
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
                                    this.query<ProductRealm>("_id == $0", productId).first().find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.plus(price)
                                }
                            }
                        } else {
                            val products = this.query<ProductRealm>().find()

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
                                    this.query<ProductRealm>("_id == $0", productId).first().find()

                                if (product != null) {
                                    product.productPrice = product.productPrice.minus(price)
                                }
                            }
                        } else {
                            val products = this.query<ProductRealm>().find()

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

                CoroutineScope(Dispatchers.IO).launch {
                    realm.write {
                        products.forEach { product ->

                            val productRealm = this.query<ProductRealm>(
                                "_id == $0 OR productName == $1 AND productPrice == $2",
                                product.productId,
                                product.productName,
                                product.productPrice
                            ).first().find()

                            if (productRealm == null) {
                                val newProduct = ProductRealm()
                                newProduct._id = product.productId
                                newProduct.productName = product.productName
                                newProduct.productPrice = product.productPrice
                                newProduct.productAvailability = product.productAvailability
                                newProduct.updated_at = System.currentTimeMillis().toString()

                                val category = this.query<Category>(
                                    "_id == $0",
                                    product.category.categoryId
                                ).first().find()

                                if (category != null) {
                                    newProduct.category = category
                                } else {
                                    val newCategory = Category()
                                    newCategory.categoryId = product.category.categoryId
                                    newCategory.categoryName = product.category.categoryName
                                    newCategory.categoryAvailability =
                                        product.category.categoryAvailability
                                    newCategory.updatedAt = System.currentTimeMillis().toString()

                                    this.copyToRealm(newCategory)

                                    newProduct.category = newCategory
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

    override suspend fun exportProducts(limit: Int?): Resource<List<ProductRealm>> {
        return Resource.Success(emptyList())
    }
}