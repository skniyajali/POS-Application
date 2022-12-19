package com.niyaj.popos.domain.repository

import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.presentation.main_feed.components.product.ProductWithQuantity
import kotlinx.coroutines.flow.Flow

interface MainFeedRepository {

    suspend fun getSelectedCartOrders(): Flow<String?>

    suspend fun getAllCategories(): Flow<Resource<List<Category>>>

    suspend fun getProductsWithQuantity(limit: Int = 10): Flow<Resource<List<ProductWithQuantity>>>

    suspend fun getProducts(limit: Int) : List<ProductWithQuantity>
}