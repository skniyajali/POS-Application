package com.niyaj.popos.features.address.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressUseCases: AddressRepository,
    private val getAllAddress : GetAllAddress,
): ViewModel() {

    private val _addresses = MutableStateFlow(AddressState())
    val addresses = _addresses.asStateFlow()

    private val _selectedAddresses  =  mutableStateListOf<String>()
    val selectedAddresses: SnapshotStateList<String> = _selectedAddresses

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private var count: Int = 0

    init {
        getAllAddresses()
    }

    fun onAddressEvent(event: AddressEvent){
        when(event){

            is AddressEvent.SelectAddress -> {
                viewModelScope.launch {
                    if(_selectedAddresses.isNotEmpty() && _selectedAddresses.contains(event.addressId)){
                        _selectedAddresses.remove(event.addressId)
                    }else{
                        _selectedAddresses.add(event.addressId)
                    }
                }
            }

            is AddressEvent.DeselectAddress -> {
                _selectedAddresses.removeAll(_selectedAddresses.toList().toSet())
            }

            is AddressEvent.SelectAllAddress -> {
                count += 1

                val addresses = _addresses.value.addresses

                if (addresses.isNotEmpty()){
                    addresses.forEach { address ->
                        if (count % 2 != 0){
                            val selectedProduct = _selectedAddresses.find { it == address.addressId }

                            if (selectedProduct == null){
                                _selectedAddresses.add(address.addressId)
                            }
                        }else {
                            _selectedAddresses.remove(address.addressId)
                        }
                    }
                }
            }

            is AddressEvent.DeleteAddress -> {
                if (event.addresses.isNotEmpty()){
                    viewModelScope.launch {
                        event.addresses.forEach { address ->
                            when (val result = addressUseCases.deleteAddress(address)) {
                                is Resource.Loading -> {
                                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                                }
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.OnSuccess("Address deleted successfully"))
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete address"))
                                }
                            }
                            _selectedAddresses.remove(address)
                        }
                    }
                }
            }

            is AddressEvent.OnSearchAddress -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllAddresses(searchText = event.searchText)
                }
            }

            is AddressEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is AddressEvent.RefreshAddress -> {
                getAllAddresses()
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
            getAllAddresses(_searchText.value)
        }
    }

    private fun getAllAddresses(searchText: String = "") {
        viewModelScope.launch {
            getAllAddress.invoke(searchText).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _addresses.value = _addresses.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {addresses ->
                            _addresses.value = _addresses.value.copy(
                                addresses = addresses
                            )
                        }
                    }
                    is Resource.Error -> {
                        _addresses.value = _addresses.value.copy(
                            error = result.message
                        )
                    }
                }

            }
        }
    }

}