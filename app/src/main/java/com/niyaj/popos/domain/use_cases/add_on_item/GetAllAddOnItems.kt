package com.niyaj.popos.domain.use_cases.add_on_item

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterAddOnItem
import com.niyaj.popos.realm.add_on_items.AddOnItem
import com.niyaj.popos.realm.add_on_items.AddOnItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetAllAddOnItems(
    private val addOnItemRepository: AddOnItemRepository
) {
    suspend operator fun invoke(
        filterAddOnItem: FilterAddOnItem,
        searchText: String = ""
    ): Flow<Resource<List<AddOnItem>>>  {
        return flow {
            addOnItemRepository.getAllAddOnItems().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { data ->
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
                        ))
                    }
                    is Resource.Error -> {
                        Timber.d("Unable to get data from repository")
                        emit(Resource.Error(result.message ?: "Unable to get data from repository"))
                    }
                }
            }
        }
    }
}

