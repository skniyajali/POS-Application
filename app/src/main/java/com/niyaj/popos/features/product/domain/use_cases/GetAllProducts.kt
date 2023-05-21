package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.model.filterByCategory
import com.niyaj.popos.features.product.domain.model.filterProducts
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllProducts(
    private val productRepository: ProductRepository
) {
    operator fun invoke(searchText : String = "", selectedCategory : String = ""): Flow<Resource<List<Product>>> {
        return channelFlow {
            productRepository.getAllProducts().collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
//                        val groupedProducts = result.data?.groupBy { it.category?.categoryId }
//                        val newProducts = groupedProducts?.values?.flatten()?.sortedBy { it.productPrice }

                        val data = result.data?.let { products ->
                            products.filter { product ->
                                product.filterByCategory(selectedCategory)
                            }.filter { product ->
                                product.filterProducts(searchText)
                            }.sortedBy { it.productId }
                        }

                        send(Resource.Success(data))
                    }

                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get products from repository"))
                    }
                }
            }
        }
    }
}