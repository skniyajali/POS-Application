package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.model.searchAddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllAddOnItems(
    private val addOnItemRepository : AddOnItemRepository,
    private val ioDispatcher : CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(
        searchText : String = ""
    ) : Flow<Resource<List<AddOnItem>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                addOnItemRepository.getAllAddOnItems().collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }

                        is Resource.Success -> {
                            val data = result.data?.let { addOnItemList ->
                                addOnItemList.filter { addOnItem ->
                                    addOnItem.searchAddOnItem(searchText)
                                }
                            }

                            data?.let { addOnItems ->
                                send(Resource.Success(addOnItems))
                            }
                        }

                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from repository"))
                        }
                    }
                }
            }
        }
    }
}

