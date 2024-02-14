package com.niyaj.feature.home.components.category

sealed class MainFeedCategoryEvent{
    data class OnSelectCategory(val categoryId: String): MainFeedCategoryEvent()
}
