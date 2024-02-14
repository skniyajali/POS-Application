package com.niyaj.feature.category.add_edit

sealed class CategoryEvent {

    data class CategoryNameChanged(val categoryName: String) : CategoryEvent()

    data object CategoryAvailabilityChanged : CategoryEvent()

    data object CreateOrUpdateCategory : CategoryEvent()
}
