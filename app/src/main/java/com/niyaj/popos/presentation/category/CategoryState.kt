package com.niyaj.popos.presentation.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterCategory

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
