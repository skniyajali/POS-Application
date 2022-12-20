package com.niyaj.popos.features.product.presentation.settings.export_products

sealed class ExportProductEvent {

    data class SelectProduct(val productId: String) : ExportProductEvent()

    data class SelectProducts(val products: List<String>) : ExportProductEvent()

    object SelectAllProduct: ExportProductEvent()

    object DeselectProducts : ExportProductEvent()

    object OnChooseProduct: ExportProductEvent()

    object GetExportedProduct: ExportProductEvent()

}
