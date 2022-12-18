package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.repository.ProductRepository

class FindProductByName(private val productRepository: ProductRepository) {

    operator fun invoke(productName: String, productId: String? = null) : Boolean {
        return productRepository.findProductByName(productName, productId)
    }

}