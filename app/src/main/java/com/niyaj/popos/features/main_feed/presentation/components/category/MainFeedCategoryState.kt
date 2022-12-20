package com.niyaj.popos.features.main_feed.presentation.components.category

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.util.FilterCategory
import com.niyaj.popos.features.common.util.SortType

data class MainFeedCategoryState(
    val categories: List<Category> = emptyList(),
    val filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
