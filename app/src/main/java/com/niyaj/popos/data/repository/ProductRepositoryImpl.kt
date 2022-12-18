package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.product.ProductRealm
import com.niyaj.popos.realm.product.ProductRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepositoryImpl(
    private val productRealmDao: ProductRealmDao
) : ProductRepository {

    override suspend fun getAllProducts(): Flow<Resource<List<Product>>> {
        return flow {
            productRealmDao.getAllProducts().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { products ->
                                products.map { product ->
                                    Product(
                                        productId = product._id,
                                        category = Category(
                                            categoryId = product.category?._id ?: "",
                                            categoryName = product.category?.categoryName ?:"",
                                            categoryAvailability = product.category?.categoryAvailability ?: true,
                                            createdAt = product.category?.created_at,
                                            updatedAt = product.category?.updated_at
                                        ),
                                        productName = product.productName,
                                        productPrice = product.productPrice,
                                        productAvailability = product.productAvailability ?: true,
                                        created_at = product.created_at,
                                        updated_at = product.updated_at,
                                    )
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get products from database"))
                    }
                }
            }
        }
    }

    override suspend fun getProductById(productId: String): Resource<Product?> {
        val result = productRealmDao.getProductById(productId)

        return result.data?.let { product ->
            Resource.Success(
                Product(
                    productId = product._id,
                    category = Category(
                        categoryId = product.category?._id ?: "",
                        categoryName = product.category?.categoryName ?:"",
                        categoryAvailability = product.category?.categoryAvailability ?: true,
                        createdAt = product.category?.created_at ,
                        updatedAt = product.category?.updated_at
                    ),
                    productName = product.productName,
                    productPrice = product.productPrice,
                    productAvailability = product.productAvailability ?: true,
                    created_at = product.created_at,
                    updated_at = product.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get products from database")
    }

    override suspend fun getProductsByCategoryId(categoryId: String): Resource<Boolean> {
        return productRealmDao.getProductsByCategoryId(categoryId)
    }

    override fun findProductByName(productName: String, productId: String?): Boolean {
        return productRealmDao.findProductByName(productName, productId)
    }

    override suspend fun createNewProduct(newProduct: Product): Resource<Boolean> {
        return productRealmDao.createNewProduct(newProduct)
    }

    override suspend fun updateProduct(newProduct: Product, id: String): Resource<Boolean> {
        return productRealmDao.updateProduct(newProduct, id)
    }

    override suspend fun deleteProduct(productId: String): Resource<Boolean> {
        return productRealmDao.deleteProduct(productId)
    }

    override suspend fun increasePrice(price: Int, productList: List<String>): Resource<Boolean> {
        return productRealmDao.increasePrice(price, productList)
    }

    override suspend fun decreasePrice(price: Int, productList: List<String>): Resource<Boolean> {
        return productRealmDao.decreasePrice(price, productList)
    }

    override suspend fun importProducts(products: List<Product>): Resource<Boolean> {
        return productRealmDao.importProducts(products)
    }

    override suspend fun exportProducts(limit: Int?): Resource<List<ProductRealm>> {
        return productRealmDao.exportProducts(limit)
    }
}