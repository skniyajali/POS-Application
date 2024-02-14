package com.niyaj.feature.product.settings.product_price

sealed class ProductPriceEvent {

    data class SelectProduct(val productId: String) : ProductPriceEvent()

    data class SelectProducts(val products: List<String>) : ProductPriceEvent()

    data class OnPriceChanged(val productPrice: String) : ProductPriceEvent()

    data object SelectAllProduct : ProductPriceEvent()

    data object DeselectProducts : ProductPriceEvent()

    data object OnChooseProduct: ProductPriceEvent()

    data object IncreaseProductPrice : ProductPriceEvent()

    data object DecreaseProductPrice : ProductPriceEvent()

}
