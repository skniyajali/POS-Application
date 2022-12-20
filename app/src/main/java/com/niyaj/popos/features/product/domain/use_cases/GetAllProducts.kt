package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.util.FilterProduct
import com.niyaj.popos.util.getAllCapitalizedLetters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllProducts(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        filterProduct: FilterProduct = FilterProduct.ByCategoryId(SortType.Ascending),
        searchText: String = "",
        selectedCategory: String = "",
    ): Flow<Resource<List<Product>>> {
        return flow {
            productRepository.getAllProducts().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        emit(
                            Resource.Success(
                            result.data?.let {products ->
                                when(filterProduct.sortType){
                                    is SortType.Ascending -> {
                                        when(filterProduct){
                                            is FilterProduct.ByProductId -> products.sortedBy { it.productId }

                                            is FilterProduct.ByCategoryId -> products.sortedBy { it.category?.categoryId }

                                            is FilterProduct.ByProductName -> products.sortedBy { it.productName.lowercase() }

                                            is FilterProduct.ByProductPrice -> products.sortedBy { it.productPrice }

                                            is FilterProduct.ByProductAvailability -> products.sortedBy { it.productAvailability }

                                            is FilterProduct.ByProductDate -> products.sortedBy { it.createdAt }

                                            is FilterProduct.ByProductQuantity -> products.sortedBy { it.productPrice }

                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterProduct){
                                            is FilterProduct.ByProductId -> products.sortedByDescending { it.productId }

                                            is FilterProduct.ByCategoryId -> products.sortedByDescending { it.category?.categoryId }

                                            is FilterProduct.ByProductName -> products.sortedByDescending { it.productName.lowercase() }

                                            is FilterProduct.ByProductPrice -> products.sortedByDescending { it.productPrice }

                                            is FilterProduct.ByProductAvailability -> products.sortedByDescending { it.productAvailability }

                                            is FilterProduct.ByProductDate -> products.sortedByDescending { it.createdAt }

                                            is FilterProduct.ByProductQuantity -> products.sortedByDescending { it.productPrice }

                                        }
                                    }
                                }
                            }?.filter { product ->
                                if(selectedCategory.isNotEmpty()){
                                    product.category?.categoryId == selectedCategory
                                }else{
                                    true
                                }
                            }?.filter { product ->
                                if(searchText.isNotEmpty()){
                                    product.productName.contains(searchText, true) ||
                                    product.productPrice.toString().contains(searchText, true) ||
                                    product.productAvailability.toString().contains(searchText, true) ||
                                    getAllCapitalizedLetters(product.productName).contains(searchText, true)
                                }else{
                                    true
                                }
                            }
                        ))
                    }

                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get products from repository"))
                    }
                }
            }
        }
    }
}