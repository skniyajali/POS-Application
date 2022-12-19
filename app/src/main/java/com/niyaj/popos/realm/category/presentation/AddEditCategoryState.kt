package com.niyaj.popos.realm.category.presentation

data class AddEditCategoryState(
    val categoryName: String = "",

    val categoryNameError: String? = null,

    val categoryAvailability: Boolean = true,
)
