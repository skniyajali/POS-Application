package com.niyaj.popos.features.delivery_partner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.delivery_partner.domain.use_cases.PartnerUseCases
import com.niyaj.popos.features.delivery_partner.domain.util.FilterPartner
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

    private val _state = MutableStateFlow(PartnerState())
    val state = _state.asStateFlow()

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
                if(_state.value.filterPartner::class == event.filterPartner::class &&
                    _state.value.filterPartner.sortType == event.filterPartner.sortType
                ){
                    _state.value = _state.value.copy(
                        filterPartner = FilterPartner.ByPartnerId(SortType.Descending)
                    )
                    return
                }

                _state.value = _state.value.copy(
                    filterPartner = event.filterPartner
                )

                getAllPartners(event.filterPartner)
            }

            is PartnerEvent.OnSearchPartner -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllPartners(
                        _state.value.filterPartner,
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
                getAllPartners(_state.value.filterPartner)
            }
        }
    }

    private fun getAllPartners(filterPartner: FilterPartner, searchText: String = "") {
        viewModelScope.launch {
            partnerUseCases.getAllPartners(filterPartner, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                partners = it,
                                filterPartner = filterPartner
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
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
                _state.value.filterPartner,
                _searchText.value
            )
        }
    }
}