package com.niyaj.popos.realm.category.presentation

import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.category.domain.util.FilterCategory
import com.niyaj.popos.realm.category.domain.model.Category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
    val isLoading: Boolean = false,
    val error: String? = null
)
