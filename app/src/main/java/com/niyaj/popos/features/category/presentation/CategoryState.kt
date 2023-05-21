package com.niyaj.popos.features.category.presentation

import com.niyaj.popos.features.category.domain.model.Category

/**
 * Category state class for displaying the category list
 * @author Sk Niyaj Ali
 * @property categories [List]
 * @property isLoading [Boolean]
 * @property error [String]
 */
data class CategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
