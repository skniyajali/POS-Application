package com.niyaj.popos.domain.use_cases.main_feed

import com.niyaj.popos.domain.repository.MainFeedRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterProduct
import com.niyaj.popos.presentation.main_feed.components.product.ProductWithQuantity
import com.niyaj.popos.util.getAllCapitalizedLetters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMainFeedProducts (
    private val mainFeedRepository: MainFeedRepository
) {
    suspend operator fun invoke(
        filterProduct: FilterProduct,
        selectedCategory: String = "",
        searchText: String = "",
    ): Flow<Resource<List<ProductWithQuantity>>>{
        return flow {
            mainFeedRepository.getProductsWithQuantity().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { products ->
                                when(filterProduct.sortType){
                                    is SortType.Ascending -> {
                                        when(filterProduct){
                                            is FilterProduct.ByProductId -> products.sortedBy { it.product.productId }

                                            is FilterProduct.ByCategoryId -> products.sortedBy { it.product.category.categoryId }

                                            is FilterProduct.ByProductName -> products.sortedBy { it.product.productName.lowercase() }

                                            is FilterProduct.ByProductPrice -> products.sortedBy { it.product.productPrice }

                                            is FilterProduct.ByProductAvailability -> products.sortedBy { it.product.productAvailability }

                                            is FilterProduct.ByProductDate -> products.sortedBy { it.product.created_at }

                                            is FilterProduct.ByProductQuantity -> products.sortedBy { it.quantity }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterProduct){
                                            is FilterProduct.ByProductId -> products.sortedByDescending { it.product.productId }

                                            is FilterProduct.ByCategoryId -> products.sortedByDescending { it.product.category.categoryId }

                                            is FilterProduct.ByProductName -> products.sortedByDescending { it.product.productName.lowercase() }

                                            is FilterProduct.ByProductPrice -> products.sortedByDescending { it.product.productPrice }

                                            is FilterProduct.ByProductAvailability -> products.sortedByDescending { it.product.productAvailability }

                                            is FilterProduct.ByProductDate -> products.sortedByDescending { it.product.created_at }

                                            is FilterProduct.ByProductQuantity -> products.sortedByDescending { it.quantity }

                                        }
                                    }
                                }
                            }?.filter { product ->
                                if(selectedCategory.isNotEmpty()){
                                    product.product.category.categoryId == selectedCategory
                                }else{
                                    true
                                }
                            }?.filter { product ->
                                if(searchText.isNotEmpty()){
                                    product.product.productName.contains(searchText, true) ||
                                            product.product.productPrice.toString().contains(searchText, true) ||
                                            product.product.productAvailability.toString().contains(searchText, true) ||
                                            getAllCapitalizedLetters(product.product.productName).contains(searchText, true)
                                }else{
                                    true
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to load products"))
                    }
                }
            }
        }
    }
}