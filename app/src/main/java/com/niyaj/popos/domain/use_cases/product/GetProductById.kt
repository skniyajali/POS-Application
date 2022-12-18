package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class GetProductById(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Resource<Product?> {
        return productRepository.getProductById(productId)
    }
}