package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class GetProductsByCategoryId(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Boolean> {
        return productRepository.getProductsByCategoryId(categoryId)
    }
}