package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.main_feed.domain.model.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.domain.model.filterByCategory
import com.niyaj.popos.features.main_feed.domain.model.filterBySearch
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetMainFeedProducts @Inject constructor(
    private val mainFeedRepository : MainFeedRepository
) {
    suspend operator fun invoke(
        selectedCategory: String = "",
        searchText: String = ""
    ): Flow<Resource<List<ProductWithFlowQuantity>>> = channelFlow {
        mainFeedRepository.geMainFeedProducts().collect { result ->
            when (result) {
                is Resource.Loading -> send(Resource.Loading(result.isLoading))
                is Resource.Success -> {
                    val filteredProducts = result.data
                        ?.filter {
                            it.filterByCategory(selectedCategory)
                        }?.filter {
                            it.filterBySearch(searchText)
                        }

                    filteredProducts?.let { send(Resource.Success(it)) }
                }
                is Resource.Error -> send(Resource.Error(result.message ?: "Unable to get products"))
            }
        }
    }
}