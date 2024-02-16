package com.niyaj.feature.address.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.model.Address
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val validationRepository: AddressValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val addressId = savedStateHandle.get<String>("addressId") ?: ""

    var state by mutableStateOf(AddEditAddressState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("addressId")?.let { addressId ->
            getAddressById(addressId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { state.addressName }
        .mapLatest {
            validationRepository.validateAddressName(it, addressId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val shortNameError: StateFlow<String?> = snapshotFlow { state.shortName }
        .mapLatest {
            validationRepository.validateAddressShortName(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onAddressEvent(event: AddEditAddressEvent) {
        when (event) {
            is AddEditAddressEvent.AddressNameChanged -> {
                state = state.copy(
                    addressName = event.addressName,
                    shortName = getAllCapitalizedLetters(event.addressName)
                )
            }

            is AddEditAddressEvent.ShortNameChanged -> {
                state = state.copy(shortName = event.shortName)
            }

            is AddEditAddressEvent.CreateOrUpdateAddress -> {
                createOrUpdateAddress(addressId)
            }
        }
    }

    private fun getAddressById(addressId: String) {
        viewModelScope.launch {
            when (val result = addressRepository.getAddressById(addressId)) {
                is Resource.Success -> {
                    result.data.let { address ->
                        if (address != null) {
                            state = state.copy(
                                shortName = address.shortName,
                                addressName = address.addressName
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to get address by ID"))
                }
            }
        }
    }

    private fun createOrUpdateAddress(addressId: String) {
        viewModelScope.launch {
            if (nameError.value == null && shortNameError.value == null) {
                val newAddress = Address(
                    shortName = state.shortName.uppercase(),
                    addressName = state.addressName.capitalizeWords,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (addressId.isEmpty()) null else System.currentTimeMillis()
                        .toString()
                )
                val message = if (addressId.isEmpty()) "created" else "updated"

                when (val result = addressRepository.createOrUpdateAddress(newAddress, addressId)) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Address $message successfully"))
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(result.message ?: "Unable to $message address")
                        )
                    }
                }

                state = AddEditAddressState()
            }
        }
    }
}