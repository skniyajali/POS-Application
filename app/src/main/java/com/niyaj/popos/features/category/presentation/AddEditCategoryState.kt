package com.niyaj.popos.features.category.presentation

data class AddEditCategoryState(
    val categoryName: String = "",

    val categoryNameError: String? = null,

    val categoryAvailability: Boolean = true,
)
