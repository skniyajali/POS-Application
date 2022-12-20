package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.product.domain.repository.ProductRepository

class FindProductByName(private val productRepository: ProductRepository) {

    operator fun invoke(productName: String, productId: String? = null) : Boolean {
        return productRepository.findProductByName(productName, productId)
    }

}