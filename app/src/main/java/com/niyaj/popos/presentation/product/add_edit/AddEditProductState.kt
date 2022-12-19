package com.niyaj.popos.presentation.product.add_edit

import com.niyaj.popos.realm.category.domain.model.Category

data class AddEditProductState (
    val productName: String = "",
    val productNameError: String? = null,

    val category: Category = Category(),
    val categoryError: String? = null,

    val productPrice: String = "",
    val productPriceError: String? = null,

    val productAvailability: Boolean = true,
)
