package com.niyaj.feature.product.settings.export_products

sealed class ExportProductEvent {

    data class SelectProduct(val productId: String) : ExportProductEvent()

    data class SelectProducts(val products: List<String>) : ExportProductEvent()

    data object SelectAllProduct: ExportProductEvent()

    data object DeselectProducts : ExportProductEvent()

    data object OnChooseProduct: ExportProductEvent()

    data object GetExportedProduct: ExportProductEvent()

}
