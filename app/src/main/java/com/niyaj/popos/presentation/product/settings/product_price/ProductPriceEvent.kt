package com.niyaj.popos.presentation.product.settings.product_price

import com.niyaj.popos.presentation.product.ProductEvent

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
