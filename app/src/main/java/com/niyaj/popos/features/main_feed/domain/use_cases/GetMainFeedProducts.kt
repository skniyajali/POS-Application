package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.main_feed.data.repository.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.data.repository.getAscendingComparator
import com.niyaj.popos.features.main_feed.data.repository.getDescendingComparator
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.niyaj.popos.util.getAllCapitalizedLetters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class GetMainFeedProducts(
    private val mainFeedRepository : MainFeedRepository
) {
    suspend operator fun invoke(
        filterProduct: FilterProduct,
        selectedCategory: String = "",
        searchText: String = ""
    ): Flow<Resource<List<ProductWithFlowQuantity>>> = channelFlow {
        withContext(Dispatchers.IO) {
            mainFeedRepository.geMainFeedProducts().collect { result ->
                when (result) {
                    is Resource.Loading -> send(result)
                    is Resource.Success -> {
                        val filteredProducts = result.data
                            ?.filter {
                                selectedCategory.isEmpty() || it.product.category?.categoryId == selectedCategory
                            }?.filter {
                                searchText.isEmpty() ||
                                        it.product.productName.contains(searchText, true) ||
                                        it.product.productPrice.toString().contains(searchText, true) ||
                                        it.product.productAvailability.toString().contains(searchText, true) ||
                                        getAllCapitalizedLetters(it.product.productName).contains(searchText, true)
                            }?.let { products ->
                                val comparator = when (filterProduct.sortType) {
                                    is SortType.Ascending -> filterProduct.getAscendingComparator()
                                    is SortType.Descending -> filterProduct.getDescendingComparator()
                                }
                                products.sortedWith(comparator)
                            }

                        filteredProducts?.let { send(Resource.Success(it)) }
                    }
                    is Resource.Error -> send(result)
                }
            }
        }
    }

//    suspend operator fun invoke(
//        filterProduct : FilterProduct,
//        selectedCategory : String = "",
//        searchText : String = "",
//    ) : Flow<Resource<List<ProductWithFlowQuantity>>> {
//        return channelFlow {
//            withContext(Dispatchers.IO) {
//                mainFeedRepository.geMainFeedProducts().collectLatest { result ->
//                    when (result) {
//                        is Resource.Loading -> {
//                            send(Resource.Loading(result.isLoading))
//                        }
//
//                        is Resource.Success -> {
//                            val filteredProducts = result.data?.filter { product ->
//                                selectedCategory.isEmpty() || product.product.category?.categoryId == selectedCategory
//                            }?.filter { product ->
//                                searchText.isEmpty() ||
//                                        product.product.productName.contains(searchText, true) ||
//                                        product.product.productPrice.toString().contains(searchText, true) ||
//                                        product.product.productAvailability.toString().contains(searchText, true) ||
//                                        getAllCapitalizedLetters(product.product.productName).contains(searchText, true)
//                            }?.let { products ->
//                                when (filterProduct.sortType) {
//                                    is SortType.Ascending -> when (filterProduct) {
//                                        is FilterProduct.ByProductId -> products.sortedBy { it.product.productId }
//                                        is FilterProduct.ByCategoryId -> products.sortedBy { it.product.category?.categoryId }
//                                        is FilterProduct.ByProductName -> products.sortedBy { it.product.productName.lowercase() }
//                                        is FilterProduct.ByProductPrice -> products.sortedBy { it.product.productPrice }
//                                        is FilterProduct.ByProductAvailability -> products.sortedBy { it.product.productAvailability }
//                                        is FilterProduct.ByProductDate -> products.sortedBy { it.product.createdAt }
//                                        is FilterProduct.ByProductQuantity -> products.sortedBy { it.product.productId }
//                                    }
//                                    is SortType.Descending -> when (filterProduct) {
//                                        is FilterProduct.ByProductId -> products.sortedByDescending { it.product.productId }
//                                        is FilterProduct.ByCategoryId -> products.sortedByDescending { it.product.category?.categoryId }
//                                        is FilterProduct.ByProductName -> products.sortedByDescending { it.product.productName.lowercase() }
//                                        is FilterProduct.ByProductPrice -> products.sortedByDescending { it.product.productPrice }
//                                        is FilterProduct.ByProductAvailability -> products.sortedByDescending { it.product.productAvailability }
//                                        is FilterProduct.ByProductDate -> products.sortedByDescending { it.product.createdAt }
//                                        is FilterProduct.ByProductQuantity -> products.sortedByDescending { it.product.productId }
//                                    }
//                                }
//
//                                val comparator = when (filterProduct.sortType) {
//                                    is SortType.Ascending -> filterProduct.getAscendingComparator()
//                                    is SortType.Descending -> filterProduct.getDescendingComparator()
//                                }
//                            }
//
//
//                            send(Resource.Success(filteredProducts))
//                        }
//
//                        is Resource.Error -> {
//                            send(Resource.Error(result.message ?: "Unable to load products"))
//                        }
//                    }
//                }
//            }
//        }
//    }
}