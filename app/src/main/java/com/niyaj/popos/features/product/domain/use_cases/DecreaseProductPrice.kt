package com.niyaj.popos.features.product.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.product.domain.repository.ProductRepository

class DecreaseProductPrice(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(price: Int, productList: List<String> = emptyList()): Resource<Boolean> {
        return productRepository.decreasePrice(price, productList)
    }
}