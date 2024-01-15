package com.niyaj.popos.features.address.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.Constants
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import com.niyaj.popos.features.address.presentation.AddressState
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
class AddressSettingViewModel @Inject constructor(
    private val addressRepository : AddressRepository,
    private val getAllAddress : GetAllAddress
): ViewModel() {

    private val _exportState = MutableStateFlow(AddressState())
    val exportState = _exportState.asStateFlow()

    private val _importExportAddresses = MutableStateFlow<List<Address>>(emptyList())
    val importExportAddresses = _importExportAddresses.asStateFlow()

    private val _selectedAddresses = mutableStateListOf<String>()
    val selectedAddresses: SnapshotStateList<String> = _selectedAddresses

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 1

    fun onEvent(event : AddressSettingEvent) {
        when(event) {
            is AddressSettingEvent.SelectAddress -> {
                viewModelScope.launch {
                    if(_selectedAddresses.contains(event.addressId)){
                        _selectedAddresses.remove(event.addressId)
                    }else{
                        _selectedAddresses.add(event.addressId)
                    }
                }
            }

            is AddressSettingEvent.SelectAllAddress -> {
                count += 1

                val addresses = when(event.type) {
                    Constants.ImportExportType.IMPORT -> _importExportAddresses.value
                    Constants.ImportExportType.EXPORT -> _exportState.value.addresses
                }

                if (addresses.isNotEmpty()){
                    addresses.forEach { address ->
                        if (count % 2 != 0){

                            val selectedAddress = _selectedAddresses.find { it == address.addressId }

                            if (selectedAddress == null){
                                _selectedAddresses.add(address.addressId)
                            }
                        }else {
                            _selectedAddresses.remove(address.addressId)
                        }
                    }
                }
            }

            is AddressSettingEvent.DeselectAddresses -> {
                _selectedAddresses.clear()
            }

            is AddressSettingEvent.OnChooseAddress -> {
                onChoose = !onChoose
            }

            is AddressSettingEvent.ImportAddresses -> {
                val addresses = mutableStateListOf<Address>()

                _selectedAddresses.forEach {
                    val data = _importExportAddresses.value.find { address -> address.addressId == it }
                    if (data != null) addresses.add(data)
                }

                viewModelScope.launch {
                    when (val result = addressRepository.importAddresses(addresses.toList())){
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("${addresses.toList().size} addresses imported successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to import addresses"))
                        }
                    }
                }
            }

            is AddressSettingEvent.ImportAddressesData -> {
                viewModelScope.launch {
                    _importExportAddresses.value = emptyList()

                    if (event.addresses.isNotEmpty()) {
                        _importExportAddresses.value = event.addresses
                        _selectedAddresses.addAll(event.addresses.map { it.addressId })
                    }
                }
            }

            is AddressSettingEvent.ClearImportedAddresses -> {
                _importExportAddresses.value = emptyList()
                _selectedAddresses.clear()
                onChoose = false
            }

            is AddressSettingEvent.GetAllAddress -> {
                getAllAddresses()
            }

            is AddressSettingEvent.GetExportedAddress -> {
                viewModelScope.launch {
                    if (_selectedAddresses.isEmpty()){
                        _importExportAddresses.value = _exportState.value.addresses
                    } else {
                        val addresses = mutableListOf<Address>()

                        _selectedAddresses.forEach { id ->
                            val address = _exportState.value.addresses.find { it.addressId == id }
                            if (address != null){
                                addresses.add(address)
                            }
                        }

                        _importExportAddresses.value = addresses.toList()
                    }
                }
            }

            is AddressSettingEvent.DeleteAllAddress -> {
                viewModelScope.launch {
                    when (val result = addressRepository.deleteAllAddress()) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("All addresses has been deleted"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete all addresses"))
                        }
                    }
                }
            }

        }
    }

    private fun getAllAddresses() {
        viewModelScope.launch {
            getAllAddress.invoke().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        _exportState.value = _exportState.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let { addresses ->
                            _exportState.value = _exportState.value.copy(
                                addresses = addresses
                            )
                        }
                    }
                    is Resource.Error -> {
                        _exportState.value = _exportState.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}