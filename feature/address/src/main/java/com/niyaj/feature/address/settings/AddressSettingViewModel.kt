package com.niyaj.feature.address.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.model.Address
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSettingViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
) : BaseViewModel() {

    val addresses = snapshotFlow { mSearchText.value }.flatMapLatest {
        addressRepository.getAllAddress(it)
    }.mapLatest { list ->
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<Address>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Address>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    var onChoose by mutableStateOf(false)

    fun onEvent(event: AddressSettingsEvent) {
        when (event) {
            is AddressSettingsEvent.ClearImportedAddresses -> {
                _importedItems.value = emptyList()
                mSelectedItems.clear()
                onChoose = false
            }

            is AddressSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = addresses.value
                    } else {
                        val list = mutableListOf<Address>()

                        mSelectedItems.forEach { id ->
                            val address = addresses.value.find { it.addressId == id }

                            if (address != null) {
                                list.add(address)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is AddressSettingsEvent.OnImportAddressItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.addressId }
                        _importedItems.value = event.data
                    }
                }
            }

            is AddressSettingsEvent.ImportAddressItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { addressId ->
                            _importedItems.value.filter { it.addressId == addressId }
                        }
                    } else {
                        _importedItems.value
                    }

                    when (val result = addressRepository.importAddresses(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("${data.size} items has been imported successfully"))
                        }
                    }
                }
            }

            is AddressSettingsEvent.OnChooseItems -> {
                onChoose = !onChoose
            }
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            val addressIds = addressRepository.getAllAddress("").first().map {
                it.addressId
            }

            val result = addressRepository.deleteAddresses(addressIds)

            when (result) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error("Unable to delete addresses"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success("${addressIds.size} addresses has been deleted.")
                    )

                }

            }
        }
    }
}