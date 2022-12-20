package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.repository.ProductRepository

class GetProductsByCategoryId(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Boolean> {
        return productRepository.getProductsByCategoryId(categoryId)
    }
}