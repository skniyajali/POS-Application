package com.niyaj.popos.presentation.main_feed.components.category

import com.niyaj.popos.realm.category.domain.util.FilterCategory

sealed class MainFeedCategoryEvent{
    data class OnSelectCategory(val categoryId: String): MainFeedCategoryEvent()

    data class OnFilterCategory(val filterCategory: FilterCategory): MainFeedCategoryEvent()

}
