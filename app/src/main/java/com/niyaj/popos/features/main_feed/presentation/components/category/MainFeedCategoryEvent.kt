package com.niyaj.popos.features.main_feed.presentation.components.category

sealed class MainFeedCategoryEvent{
    data class OnSelectCategory(val categoryId: String): MainFeedCategoryEvent()
}
