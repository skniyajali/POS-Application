package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductOrder
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun getAllCategories(): Flow<List<Category>>

    suspend fun getCategoryById(categoryId: String): Category?

    suspend fun getAllProducts(searchText: String, categoryId: String): Flow<List<Product>>

    suspend fun getProductById(productId: String): Resource<Product?>

    suspend fun findProductByName(productName: String, productId: String? = null): Boolean

    suspend fun createOrUpdateProduct(newProduct: Product, productId: String): Resource<Boolean>

    suspend fun deleteProducts(productIds: List<String>): Resource<Boolean>

    suspend fun increasePrice(price: Int, productList: List<String>): Resource<Boolean>

    suspend fun decreasePrice(price: Int, productList: List<String>): Resource<Boolean>

    suspend fun importProducts(products: List<Product>): Resource<Boolean>

    suspend fun getProductOrders(productId: String): Flow<List<ProductOrder>>

}