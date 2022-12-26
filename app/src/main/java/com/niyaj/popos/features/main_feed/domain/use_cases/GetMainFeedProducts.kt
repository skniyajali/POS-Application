package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.niyaj.popos.util.getAllCapitalizedLetters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetMainFeedProducts(
    private val mainFeedRepository: MainFeedRepository
) {
    suspend operator fun invoke(
        filterProduct: FilterProduct,
        selectedCategory: String = "",
        searchText: String = "",
    ): Flow<Resource<List<ProductWithQuantity>>> {
        return channelFlow {
            mainFeedRepository.getProductsWithQuantity().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        val data = result.data?.let { products ->
                            when (filterProduct.sortType) {
                                is SortType.Ascending -> {
                                    when (filterProduct) {
                                        is FilterProduct.ByProductId -> products.sortedBy { it.product.productId }

                                        is FilterProduct.ByCategoryId -> products.sortedBy { it.product.category?.categoryId }

                                        is FilterProduct.ByProductName -> products.sortedBy { it.product.productName.lowercase() }

                                        is FilterProduct.ByProductPrice -> products.sortedBy { it.product.productPrice }

                                        is FilterProduct.ByProductAvailability -> products.sortedBy { it.product.productAvailability }

                                        is FilterProduct.ByProductDate -> products.sortedBy { it.product.createdAt }

                                        is FilterProduct.ByProductQuantity -> products.sortedBy { it.quantity }
                                    }
                                }

                                is SortType.Descending -> {
                                    when (filterProduct) {
                                        is FilterProduct.ByProductId -> products.sortedByDescending { it.product.productId }

                                        is FilterProduct.ByCategoryId -> products.sortedByDescending { it.product.category?.categoryId }

                                        is FilterProduct.ByProductName -> products.sortedByDescending { it.product.productName.lowercase() }

                                        is FilterProduct.ByProductPrice -> products.sortedByDescending { it.product.productPrice }

                                        is FilterProduct.ByProductAvailability -> products.sortedByDescending { it.product.productAvailability }

                                        is FilterProduct.ByProductDate -> products.sortedByDescending { it.product.createdAt }

                                        is FilterProduct.ByProductQuantity -> products.sortedByDescending { it.quantity }

                                    }
                                }
                            }
                        }?.filter { product ->
                            if (selectedCategory.isNotEmpty()) {
                                product.product.category?.categoryId == selectedCategory
                            } else if (searchText.isNotEmpty()) {
                                product.product.productName.contains(searchText, true) ||
                                        product.product.productPrice.toString()
                                            .contains(searchText, true) ||
                                        product.product.productAvailability.toString()
                                            .contains(searchText, true) ||
                                        getAllCapitalizedLetters(product.product.productName).contains(
                                            searchText,
                                            true
                                        )
                            } else {
                                true
                            }
                        }
                        send(Resource.Success(data))
                    }

                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to load products"))
                    }
                }
            }
        }
    }
}