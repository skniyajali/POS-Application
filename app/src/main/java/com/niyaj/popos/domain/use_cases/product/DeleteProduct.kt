package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class DeleteProduct(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productId: String): Resource<Boolean> {
        return productRepository.deleteProduct(productId)
    }
}