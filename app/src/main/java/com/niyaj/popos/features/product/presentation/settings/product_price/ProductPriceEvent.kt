package com.niyaj.popos.features.product.presentation.settings.product_price

sealed class ProductPriceEvent {

    data class SelectProduct(val productId: String) : ProductPriceEvent()

    data class SelectProducts(val products: List<String>) : ProductPriceEvent()

    object SelectAllProduct : ProductPriceEvent()

    object DeselectProducts : ProductPriceEvent()

    data class OnPriceChanged(val productPrice: String) : ProductPriceEvent()

    object OnChooseProduct: ProductPriceEvent()

    object IncreaseProductPrice : ProductPriceEvent()

    object DecreaseProductPrice : ProductPriceEvent()

}
