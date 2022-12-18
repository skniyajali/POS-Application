package com.niyaj.popos.presentation.main_feed.components.category

import com.niyaj.popos.domain.util.filter_items.FilterCategory

sealed class MainFeedCategoryEvent{
    data class OnSelectCategory(val categoryId: String): MainFeedCategoryEvent()

    data class OnFilterCategory(val filterCategory: FilterCategory): MainFeedCategoryEvent()

}
