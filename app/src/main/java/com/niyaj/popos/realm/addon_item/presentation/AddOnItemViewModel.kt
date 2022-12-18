package com.niyaj.popos.realm.addon_item.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.realm.addon_item.domain.use_cases.AddOnItemUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.realm.addon_item.domain.util.FilterAddOnItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOnItemViewModel @Inject constructor(
    private val addOnItemUseCases: AddOnItemUseCases,
): ViewModel() {

    private val _state = MutableStateFlow(AddOnItemState())
    val state = _state.asStateFlow()

    private val _selectedAddOnItems  =  mutableStateListOf<String>()
    val selectedAddOnItems: SnapshotStateList<String> = _selectedAddOnItems

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private var count: Int = 0

    init {
        getAllAddOnItems(FilterAddOnItem.ByAddOnItemId(SortType.Descending))
    }

    fun onAddOnItemsEvent(event: AddOnItemEvent) {
        when (event){

            is AddOnItemEvent.SelectAddOnItem -> {
                viewModelScope.launch {
                    if(_selectedAddOnItems.contains(event.addOnItemId)){
                        _selectedAddOnItems.remove(event.addOnItemId)
                    }else{
                        _selectedAddOnItems.add(event.addOnItemId)
                    }
                }
            }

            is AddOnItemEvent.SelectAllAddOnItem -> {
                count += 1

                val addOnItems = _state.value.addOnItems

                if (addOnItems.isNotEmpty()){
                    addOnItems.forEach { addOnItem ->
                        if (count % 2 != 0){
                            val selectedProduct = _selectedAddOnItems.find { it == addOnItem.addOnItemId }

                            if (selectedProduct == null){
                                _selectedAddOnItems.add(addOnItem.addOnItemId)
                            }
                        }else {
                            _selectedAddOnItems.remove(addOnItem.addOnItemId)
                        }
                    }
                }
            }

            is AddOnItemEvent.DeselectAddOnItem -> {
                _selectedAddOnItems.removeAll(_selectedAddOnItems.toList())
            }

            is AddOnItemEvent.DeleteAddOnItem -> {
                viewModelScope.launch {
                    if(event.addOnItems.isNotEmpty()){
                        event.addOnItems.forEach { addOnItem ->
                            when (val result = addOnItemUseCases.deleteAddOnItem(addOnItem)) {
                                is Resource.Loading -> {
                                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                                }
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("AddOnItem deleted successfully"))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete item"))
                                }
                            }
                            _selectedAddOnItems.remove(addOnItem)
                        }
                    }

                }
            }

            is AddOnItemEvent.OnFilterAddOnItem -> {
                if(_state.value.filterAddOnItem::class == event.filterAddOnItem::class &&
                    _state.value.filterAddOnItem.sortType == event.filterAddOnItem.sortType
                ){
                    _state.value = _state.value.copy(
                        filterAddOnItem = FilterAddOnItem.ByAddOnItemId(SortType.Descending)
                    )
                    return
                }

                _state.value = _state.value.copy(
                    filterAddOnItem = event.filterAddOnItem
                )

                getAllAddOnItems(event.filterAddOnItem)

            }

            is AddOnItemEvent.OnSearchAddOnItem -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllAddOnItems(
                        _state.value.filterAddOnItem,
                        searchText = event.searchText
                    )
                }
            }

            is AddOnItemEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }
            is AddOnItemEvent.RefreshAddOnItem -> {
                getAllAddOnItems(_state.value.filterAddOnItem)
            }
        }
    }

    private fun getAllAddOnItems(filterAddOnItem: FilterAddOnItem, searchText: String = "") {
        viewModelScope.launch {
            addOnItemUseCases.getAllAddOnItems(filterAddOnItem, searchText).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                addOnItems = it,
                                filterAddOnItem = filterAddOnItem
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = "Unable to load resources")
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllAddOnItems(
                _state.value.filterAddOnItem,
                _searchText.value
            )
        }
    }

}

