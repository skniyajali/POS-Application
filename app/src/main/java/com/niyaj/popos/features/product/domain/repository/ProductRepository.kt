package com.niyaj.popos.features.product.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.presentation.details.ProductOrder
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun getAllProducts(): Flow<Resource<List<Product>>>

    fun getProductById(productId: String): Resource<Product?>

    fun findProductByName(productName: String, productId: String? = null): Boolean

    suspend fun createNewProduct(newProduct: Product): Resource<Boolean>

    suspend fun updateProduct(newProduct: Product, productId: String): Resource<Boolean>

    suspend fun deleteProduct(productId: String): Resource<Boolean>

    suspend fun increasePrice(price: Int, productList: List<String> = emptyList()): Resource<Boolean>

    suspend fun decreasePrice(price: Int, productList: List<String> = emptyList()): Resource<Boolean>

    suspend fun importProducts(products: List<Product>): Resource<Boolean>

    suspend fun getProductOrders(productId: String): Flow<Resource<List<ProductOrder>>>

}