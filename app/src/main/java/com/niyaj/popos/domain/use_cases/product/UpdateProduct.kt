package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class UpdateProduct(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(product:Product, productId: String): Resource<Boolean> {
        return productRepository.updateProduct(product, productId)
    }
}