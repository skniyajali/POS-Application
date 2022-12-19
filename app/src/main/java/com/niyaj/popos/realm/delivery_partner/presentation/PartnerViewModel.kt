package com.niyaj.popos.realm.delivery_partner.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.realm.delivery_partner.domain.use_cases.PartnerUseCases
import com.niyaj.popos.realm.delivery_partner.domain.util.FilterPartner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PartnerViewModel @Inject constructor(
    private val partnerUseCases: PartnerUseCases
): ViewModel() {

    var state by mutableStateOf(PartnerState())

    private val _selectedPartner =  MutableStateFlow("")
    val selectedPartner = _selectedPartner.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    init {
        _selectedPartner.value = ""
        getAllPartners(FilterPartner.ByPartnerId(SortType.Descending))
    }

    fun onPartnerEvent(event: PartnerEvent) {
        when (event){

            is PartnerEvent.SelectPartner -> {
                viewModelScope.launch {
                    if(_selectedPartner.value.isNotEmpty() && _selectedPartner.value == event.partnerId){
                        _selectedPartner.emit("")
                    }else{
                        _selectedPartner.emit(event.partnerId)
                    }
                }
            }

            is PartnerEvent.DeletePartner -> {
                viewModelScope.launch {
                    when (val result = partnerUseCases.deletePartner(event.partnerId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Charges deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete charges item"))
                        }
                    }
                }
                _selectedPartner.value = ""
            }

            is PartnerEvent.OnFilterPartner -> {
                if(state.filterPartner::class == event.filterPartner::class &&
                    state.filterPartner.sortType == event.filterPartner.sortType
                ){
                    state = state.copy(
                        filterPartner = FilterPartner.ByPartnerId(SortType.Descending)
                    )
                    return
                }

                state = state.copy(
                    filterPartner = event.filterPartner
                )

                getAllPartners(event.filterPartner)
            }

            is PartnerEvent.OnSearchPartner -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllPartners(
                        state.filterPartner,
                        searchText = event.searchText
                    )
                }
            }

            is PartnerEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is PartnerEvent.RefreshPartner -> {
                getAllPartners(state.filterPartner)
            }
        }
    }

    private fun getAllPartners(filterPartner: FilterPartner, searchText: String = "") {
        viewModelScope.launch {
            partnerUseCases.getAllPartners(filterPartner, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        state = state.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            state = state.copy(
                                partners = it,
                                filterPartner = filterPartner
                            )
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(error = "Unable to load resources")
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
            getAllPartners(
                state.filterPartner,
                _searchText.value
            )
        }
    }
}