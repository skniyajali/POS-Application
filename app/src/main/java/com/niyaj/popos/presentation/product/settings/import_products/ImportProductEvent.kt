package com.niyaj.popos.presentation.product.settings.import_products

import com.niyaj.popos.domain.model.Product

sealed class ImportProductEvent {

    data class SelectProduct(val productId: String) : ImportProductEvent()

    data class SelectProducts(val products: List<String>) : ImportProductEvent()

    object SelectAllProduct : ImportProductEvent()

    object DeselectProducts : ImportProductEvent()

    object OnChooseProduct: ImportProductEvent()

    data class ImportProductsData(val products: List<Product> = emptyList()): ImportProductEvent()

    object ClearImportedProducts: ImportProductEvent()

    object ImportProducts: ImportProductEvent()
    
}
