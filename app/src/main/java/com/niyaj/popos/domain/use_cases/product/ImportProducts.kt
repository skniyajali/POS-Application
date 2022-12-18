package com.niyaj.popos.domain.use_cases.product

import com.niyaj.popos.domain.model.Product
import com.niyaj.popos.domain.repository.ProductRepository
import com.niyaj.popos.domain.util.Resource

class ImportProducts(
    private val productRepository: ProductRepository
) {

    suspend operator fun invoke(productList: List<Product>): Resource<Boolean> {
        return productRepository.importProducts(productList)
    }
}