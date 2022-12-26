package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.util.FilterCategory
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMainFeedCategories(
    private val mainFeedRepository: MainFeedRepository
) {
    suspend operator fun invoke(
        filterCategory: FilterCategory,
        searchText: String = ""
    ): Flow<Resource<List<Category>>> {
        return flow {
            mainFeedRepository.getAllCategories().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }

                    is Resource.Success -> {
                        val data = result.data?.let { categories ->
                            when (filterCategory.sortType) {
                                is SortType.Ascending -> {
                                    when (filterCategory) {
                                        is FilterCategory.ByCategoryId -> categories.sortedBy { it.categoryId }
                                        is FilterCategory.ByCategoryName -> categories.sortedBy { it.categoryName.lowercase() }
                                        is FilterCategory.ByCategoryAvailability -> categories.sortedBy { it.categoryAvailability }
                                        is FilterCategory.ByCategoryDate -> categories.sortedBy { it.createdAt }
                                    }
                                }

                                is SortType.Descending -> {
                                    when (filterCategory) {
                                        is FilterCategory.ByCategoryId -> categories.sortedByDescending { it.categoryId }
                                        is FilterCategory.ByCategoryName -> categories.sortedByDescending { it.categoryName.lowercase() }
                                        is FilterCategory.ByCategoryAvailability -> categories.sortedByDescending { it.categoryAvailability }
                                        is FilterCategory.ByCategoryDate -> categories.sortedByDescending { it.createdAt }
                                    }
                                }
                            }.filter { category ->
                                if (searchText.isNotEmpty()) {
                                    category.categoryName.contains(searchText, true) ||
                                    category.categoryAvailability.toString().contains(searchText, true) ||
                                    category.createdAt.contains(searchText, true)
                                } else {
                                    true
                                }
                            }
                        }

                        emit(Resource.Success(data))
                    }

                    is Resource.Error -> {
                        emit(
                            Resource.Error(
                                result.message ?: "Unable to load categories from repository"
                            )
                        )
                    }
                }
            }
        }
    }
}