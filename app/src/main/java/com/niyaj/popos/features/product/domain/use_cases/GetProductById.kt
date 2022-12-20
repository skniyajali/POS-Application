package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository

class GetProductById(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Resource<Product?> {
        return productRepository.getProductById(productId)
    }
}