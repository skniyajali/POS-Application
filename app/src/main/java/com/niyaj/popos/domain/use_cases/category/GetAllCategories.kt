package com.niyaj.popos.domain.use_cases.category

import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.domain.repository.CategoryRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCategories(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(
        filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
        searchText: String = ""
    ): Flow<Resource<List<Category>>> {
        return flow {
            categoryRepository.getAllCategories().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { categories ->
                                when(filterCategory.sortType){
                                    is SortType.Ascending -> {
                                        when(filterCategory){
                                            is FilterCategory.ByCategoryId -> categories.sortedBy { it.categoryId }
                                            is FilterCategory.ByCategoryName -> categories.sortedBy { it.categoryName.lowercase() }
                                            is FilterCategory.ByCategoryAvailability -> categories.sortedBy { it.categoryAvailability }
                                            is FilterCategory.ByCategoryDate -> categories.sortedBy { it.createdAt }
                                        }
                                    }

                                    is SortType.Descending -> {
                                        when(filterCategory){
                                            is FilterCategory.ByCategoryId -> categories.sortedByDescending { it.categoryId }
                                            is FilterCategory.ByCategoryName -> categories.sortedByDescending { it.categoryName.lowercase() }
                                            is FilterCategory.ByCategoryAvailability -> categories.sortedByDescending { it.categoryAvailability }
                                            is FilterCategory.ByCategoryDate -> categories.sortedByDescending { it.createdAt }
                                        }
                                    }
                                }.filter { category ->
                                    if(searchText.isNotEmpty()){
                                        category.categoryName.contains(searchText, true) ||
                                        category.categoryAvailability.toString().contains(searchText, true) ||
                                        category.createdAt?.contains(searchText, true) == true
                                    }else {
                                        true
                                    }
                                }.filter {
                                    it.categoryAvailability
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to load categories from repository"))
                    }
                }
            }
        }

    }
}