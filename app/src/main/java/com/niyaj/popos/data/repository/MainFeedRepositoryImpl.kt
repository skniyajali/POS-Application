package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.MainFeedRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.presentation.main_feed.components.product.ProductWithQuantity
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.main_feed.MainFeedService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MainFeedRepositoryImpl(
    private val mainFeedService: MainFeedService
) : MainFeedRepository {

    override suspend fun getSelectedCartOrders(): Flow<String?> {
        return mainFeedService.getSelectedCartOrders().map {
            it?.cartOrder?._id
        }
    }

    override suspend fun getAllCategories(): Flow<Resource<List<Category>>> {
        return flow{
            mainFeedService.getAllCategories().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(result.data))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all categories"))
                    }
                }
            }
        }
    }

    override suspend fun getProductsWithQuantity(limit: Int): Flow<Resource<List<ProductWithQuantity>>> {
        return flow {
            mainFeedService.getProductsWithQuantity(limit).collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            emit(Resource.Success(
                                it.map { product ->
                                    ProductWithQuantity(
                                        product = Product(
                                            productId = product.productRealm._id,
                                            category = product.productRealm.category!!,
                                            productName = product.productRealm.productName,
                                            productPrice = product.productRealm.productPrice,
                                            productAvailability = product.productRealm.productAvailability ?: true,
                                            created_at = product.productRealm.created_at,
                                            updated_at = product.productRealm.updated_at,
                                        ),
                                        quantity = product.quantity
                                    )
                                }
                            ))
                        }
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get all products"))
                    }
                }
            }
        }
    }

    override suspend fun getProducts(limit: Int): List<ProductWithQuantity> {
        return mainFeedService.getProducts(limit).map { product ->
            ProductWithQuantity(
                product = Product(
                    productId = product.productRealm._id,
                    category = product.productRealm.category!!,
                    productName = product.productRealm.productName,
                    productPrice = product.productRealm.productPrice,
                    productAvailability = product.productRealm.productAvailability ?: true,
                    created_at = product.productRealm.created_at,
                    updated_at = product.productRealm.updated_at,
                ),
                quantity = product.quantity
            )
        }
    }
}