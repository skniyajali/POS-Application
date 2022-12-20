package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository

class UpdateProduct(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(product: Product, productId: String): Resource<Boolean> {
        return productRepository.updateProduct(product, productId)
    }
}