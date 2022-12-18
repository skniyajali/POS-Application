package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.product.ProductRealm
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun getAllProducts(): Flow<Resource<List<Product>>>

    suspend fun getProductById(productId: String): Resource<Product?>

    suspend fun getProductsByCategoryId(categoryId: String): Resource<Boolean>

    fun findProductByName(productName: String, productId: String? = null): Boolean


    suspend fun createNewProduct(newProduct: Product): Resource<Boolean>

    suspend fun updateProduct(newProduct: Product, id: String): Resource<Boolean>

    suspend fun deleteProduct(productId: String): Resource<Boolean>

    suspend fun increasePrice(price: Int, productList: List<String> = emptyList()): Resource<Boolean>

    suspend fun decreasePrice(price: Int, productList: List<String> = emptyList()): Resource<Boolean>

    suspend fun importProducts(products: List<Product>): Resource<Boolean>

    suspend fun exportProducts(limit: Int? = 0): Resource<List<ProductRealm>>
}