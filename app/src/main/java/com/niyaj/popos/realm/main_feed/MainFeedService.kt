package com.niyaj.popos.realm.main_feed

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface MainFeedService {

    suspend fun getSelectedCartOrders(): Flow<SelectedCartOrderRealm?>

    suspend fun getAllCategories(): Flow<Resource<List<Category>>>

    suspend fun getProductsWithQuantity(limit: Int): Flow<Resource<List<ProductWithQuantityRealm>>>

    suspend fun getProductQuantity(cartOrderId: String, productId: String): Int

    suspend fun getProducts(limit: Int): List<ProductWithQuantityRealm>
}