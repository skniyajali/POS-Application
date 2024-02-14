package com.niyaj.feature.product.settings.import_products

import com.niyaj.model.Product

sealed class ImportProductEvent {

    data class SelectProduct(val productId: String) : ImportProductEvent()

    data class SelectProducts(val products: List<String>) : ImportProductEvent()

    data object SelectAllProduct : ImportProductEvent()

    data object DeselectProducts : ImportProductEvent()

    data object OnChooseProduct: ImportProductEvent()

    data class ImportProductsData(val products: List<Product> = emptyList()): ImportProductEvent()

    data object ClearImportedProducts: ImportProductEvent()

    data object ImportProducts: ImportProductEvent()
    
}
