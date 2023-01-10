package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllAddOnItems(
    private val addOnItemRepository: AddOnItemRepository
) {
    suspend operator fun invoke(
        filterAddOnItem: FilterAddOnItem,
        searchText: String = ""
    ): Flow<Resource<List<AddOnItem>>>  {
        return channelFlow {
            addOnItemRepository.getAllAddOnItems().collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { data ->
                            when(filterAddOnItem.sortType){
                                is SortType.Ascending -> {
                                    when(filterAddOnItem){
                                        is FilterAddOnItem.ByAddOnItemId -> {
                                            data.sortedBy { it.addOnItemId }
                                        }
                                        is FilterAddOnItem.ByAddOnItemName -> {
                                            data.sortedBy { it.itemName }
                                        }
                                        is FilterAddOnItem.ByAddOnItemPrice -> {
                                            data.sortedBy { it.itemPrice }
                                        }
                                        is FilterAddOnItem.ByAddOnItemDate -> {
                                            data.sortedBy { it.createdAt }
                                        }
                                    }
                                }
                                is SortType.Descending -> {
                                    when(filterAddOnItem){
                                        is FilterAddOnItem.ByAddOnItemId -> {
                                            data.sortedByDescending { it.addOnItemId }
                                        }
                                        is FilterAddOnItem.ByAddOnItemName -> {
                                            data.sortedByDescending { it.itemName }
                                        }
                                        is FilterAddOnItem.ByAddOnItemPrice -> {
                                            data.sortedByDescending { it.itemPrice }
                                        }
                                        is FilterAddOnItem.ByAddOnItemDate -> {
                                            data.sortedByDescending { it.createdAt }
                                        }
                                    }
                                }
                            }.filter { addOnItem ->
                                addOnItem.itemName.contains(searchText, true) ||
                                        addOnItem.itemPrice.toString().contains(searchText, true) ||
                                        addOnItem.createdAt.contains(searchText, true) ||
                                        addOnItem.updatedAt?.contains(searchText, true) == true
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get data from repository"))
                    }
                }
            }
        }
    }
}

