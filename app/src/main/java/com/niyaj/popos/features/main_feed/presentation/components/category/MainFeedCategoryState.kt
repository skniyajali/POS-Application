package com.niyaj.popos.features.main_feed.presentation.components.category

import com.niyaj.popos.features.category.domain.model.Category

data class MainFeedCategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
