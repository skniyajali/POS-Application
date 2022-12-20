package com.niyaj.popos.features.main_feed.presentation.components.category

import com.niyaj.popos.features.category.domain.util.FilterCategory

sealed class MainFeedCategoryEvent{
    data class OnSelectCategory(val categoryId: String): MainFeedCategoryEvent()

    data class OnFilterCategory(val filterCategory: FilterCategory): MainFeedCategoryEvent()

}
