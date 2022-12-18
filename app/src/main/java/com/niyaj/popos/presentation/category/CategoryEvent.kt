package com.niyaj.popos.presentation.category

import com.niyaj.popos.domain.util.filter_items.FilterCategory

sealed class CategoryEvent{

    data class CategoryNameChanged(val categoryName: String) : CategoryEvent()

    data class SelectCategory(val categoryId: String) : CategoryEvent()

    object DeselectCategories: CategoryEvent()

    object SelectAllCategories : CategoryEvent()

    object CategoryAvailabilityChanged : CategoryEvent()

    object CreateNewCategory: CategoryEvent()

    data class UpdateCategory(val categoryId: String): CategoryEvent()

    data class DeleteCategories(val categories: List<String>): CategoryEvent()

    data class OnFilterCategory(val filterCategory: FilterCategory): CategoryEvent()

    data class OnSearchCategory(val searchText: String): CategoryEvent()

    object ToggleSearchBar : CategoryEvent()

    object RefreshCategory : CategoryEvent()

}
