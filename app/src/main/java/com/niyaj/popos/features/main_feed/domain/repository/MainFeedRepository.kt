package com.niyaj.popos.features.main_feed.domain.repository

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import kotlinx.coroutines.flow.Flow

interface MainFeedRepository {

    suspend fun getSelectedCartOrders(): Flow<String?>

    suspend fun getAllCategories(): Flow<Resource<List<Category>>>

    suspend fun getProductsWithQuantity(): Flow<Resource<List<ProductWithQuantity>>>

    suspend fun getProductQuantity(cartOrderId: String, productId: String): Int

    suspend fun getProducts(limit: Int): List<ProductWithQuantity>
}