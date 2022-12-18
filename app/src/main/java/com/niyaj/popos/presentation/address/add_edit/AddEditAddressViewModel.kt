package com.niyaj.popos.presentation.address.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.use_cases.address.AddressUseCases
import com.niyaj.popos.domain.use_cases.address.validation.ValidateAddressName
import com.niyaj.popos.domain.use_cases.address.validation.ValidateAddressShortName
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.util.capitalizeWords
import com.niyaj.popos.util.getAllCapitalizedLetters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAddressViewModel @Inject constructor(
    private val validateAddressShortName: ValidateAddressShortName,
    private val validateAddressName: ValidateAddressName,
    private val addressUseCases: AddressUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var addEditAddressState by mutableStateOf(AddEditAddressState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("addressId")?.let { addressId ->
            getAddressById(addressId)
        }
    }

    fun onAddressEvent(event: AddEditAddressEvent){
        when(event){
            is AddEditAddressEvent.ShortNameChanged -> {
                addEditAddressState = addEditAddressState.copy(
                    shortName = event.shortName,
                )
            }

            is AddEditAddressEvent.AddressNameChanged -> {
                addEditAddressState = addEditAddressState.copy(
                    address = event.addressName,
                )

                addEditAddressState = addEditAddressState.copy(
                    shortName = getAllCapitalizedLetters(event.addressName),
                )
            }

            is AddEditAddressEvent.CreateNewAddress -> {
                addOrEditAddress()
            }

            is AddEditAddressEvent.UpdateAddress -> {
                addOrEditAddress(event.addressId)
            }
        }
    }



    private fun getAddressById(addressId: String){
        viewModelScope.launch {
            when(val result = addressUseCases.getAddressById(addressId))
            {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data.let {address ->
                        if (address != null) {
                            addEditAddressState = addEditAddressState.copy(
                                shortName = address.shortName,
                                address = address.addressName
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get address by ID"))
                }
            }
        }
    }

    private fun addOrEditAddress(addressId: String? = null){

        val validatedShortName = validateAddressShortName.execute(addEditAddressState.shortName)
        val validatedAddressName = validateAddressName.execute(addEditAddressState.address)

        val hasError = listOf(validatedAddressName,validatedShortName).any {
            !it.successful
        }

        if (hasError) {
            addEditAddressState = addEditAddressState.copy(
                shortNameError = validatedShortName.errorMessage,
                addressError = validatedAddressName.errorMessage,
            )
            return
        }else{
            viewModelScope.launch {
                if (addressId.isNullOrEmpty()){
                    val result = addressUseCases.createNewAddress(
                        Address(
                            shortName = addEditAddressState.shortName.uppercase(),
                            addressName = addEditAddressState.address.capitalizeWords
                        )
                    )
                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Address created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create address"))
                        }
                    }
                    addEditAddressState = AddEditAddressState()

                }else{
                    val result = addressUseCases.updateAddress(
                        Address(
                            shortName = addEditAddressState.shortName.uppercase(),
                            addressName = addEditAddressState.address.capitalizeWords
                        ),
                        addressId
                    )
                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Address updated successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update address"))
                        }
                    }

                    addEditAddressState = AddEditAddressState()
                }
            }
        }

    }
    
}