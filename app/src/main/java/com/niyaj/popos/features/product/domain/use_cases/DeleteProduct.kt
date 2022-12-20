package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.repository.ProductRepository

class DeleteProduct(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String): Resource<Boolean> {
        return productRepository.deleteProduct(productId)
    }
}