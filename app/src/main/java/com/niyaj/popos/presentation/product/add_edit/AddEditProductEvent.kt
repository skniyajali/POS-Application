package com.niyaj.popos.presentation.product.add_edit

import com.niyaj.popos.domain.model.Category

sealed class AddEditProductEvent {

    data class ProductNameChanged(val productName: String) : AddEditProductEvent()

    data class ProductPriceChanged(val productPrice: String) : AddEditProductEvent()

    data class CategoryNameChanged(val category: Category) : AddEditProductEvent()

    object ProductAvailabilityChanged : AddEditProductEvent()

    data class UpdateProduct(val productId: String) : AddEditProductEvent()

    object CreateNewProduct : AddEditProductEvent()

}
