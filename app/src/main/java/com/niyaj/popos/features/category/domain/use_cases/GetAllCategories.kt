package com.niyaj.popos.features.category.domain.use_cases

import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.model.filterCategory
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllCategories(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(searchText : String = ""): Flow<Resource<List<Category>>> {
        return channelFlow {
            categoryRepository.getAllCategories().collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { categories ->
                            categories.filter { category ->
                                category.filterCategory(searchText)
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to load categories from repository"))
                    }
                }
            }
        }
    }
}