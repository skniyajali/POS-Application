package com.niyaj.feature.home.components.category

import com.niyaj.model.Category

data class MainFeedCategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
