package com.niyaj.popos.presentation.category

data class AddEditCategoryState(
    val categoryName: String = "",

    val categoryNameError: String? = null,

    val categoryAvailability: Boolean = true,
)
