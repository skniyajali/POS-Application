package com.niyaj.popos.presentation.delivery_partner.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.model.DeliveryPartner
import com.niyaj.popos.domain.use_cases.delivery_partner.PartnerUseCases
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerEmail
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerName
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerPassword
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerPhone
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPartnerViewModel @Inject constructor(
    private val validatePartnerName: ValidatePartnerName,
    private val validatePartnerEmail: ValidatePartnerEmail,
    private val validatePartnerPhone: ValidatePartnerPhone,
    private val validatePartnerPassword: ValidatePartnerPassword,
    private val partnerUseCases: PartnerUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var addEditState by mutableStateOf(AddEditPartnerState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _passwordToggle = mutableStateOf(false)
    val passwordToggle: State<Boolean> = _passwordToggle

    var expanded by mutableStateOf(false)

    init {
        savedStateHandle.get<String>("partnerId")?.let { partnerId ->
            if(partnerId.isNotEmpty()) {
                viewModelScope.launch {
                    when(val result = partnerUseCases.getPartnerById(partnerId)) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            result.data?.let {
                                addEditState = addEditState.copy(
                                    partnerName = it.deliveryPartnerName,
                                    partnerEmail = it.deliveryPartnerEmail,
                                    partnerPhone = it.deliveryPartnerPhone,
                                    partnerPassword = it.deliveryPartnerPassword,
                                    partnerStatus = it.deliveryPartnerStatus,
                                    partnerType = it.deliveryPartnerType,
                                )
                            }
                        }
                        is Resource.Error -> {}
                    }
                }
            }
        }
    }

    fun onAddEditPartnerEvent(event: AddEditPartnerEvent) {
        when (event){
            is AddEditPartnerEvent.PartnerNameChanged -> {
                addEditState = addEditState.copy(partnerName = event.partnerName)
            }

            is AddEditPartnerEvent.PartnerPhoneChanged -> {
                addEditState = addEditState.copy(partnerPhone = event.partnerPhone)
            }
            is AddEditPartnerEvent.PartnerEmailChanged -> {
                addEditState = addEditState.copy(partnerEmail = event.partnerEmail)
            }
            is AddEditPartnerEvent.PartnerPasswordChanged -> {
                addEditState = addEditState.copy(partnerPassword = event.partnerPassword)
            }
            is AddEditPartnerEvent.PartnerStatusChanged -> {
                addEditState = addEditState.copy(partnerStatus = event.partnerStatus)
            }
            is AddEditPartnerEvent.PartnerTypeChanged -> {
                addEditState = addEditState.copy(partnerType = event.partnerType)
            }

            is AddEditPartnerEvent.CreateNewPartner -> {
                addOrEditPartner()
            }
            is AddEditPartnerEvent.UpdatePartner -> {
                addOrEditPartner(event.partnerId)
            }
        }

    }

    private fun addOrEditPartner(partnerId: String? = null){
        viewModelScope.launch {
            val validatedPartnerName = validatePartnerName.execute(addEditState.partnerName)
            val validatedPartnerPhone = validatePartnerPhone.execute(addEditState.partnerPhone, partnerId)
            val validatedPartnerEmail = validatePartnerEmail.execute(addEditState.partnerEmail, partnerId)
            val validatedPartnerPassword = validatePartnerPassword.execute(addEditState.partnerPassword)

            val hasError = listOf(validatedPartnerName, validatedPartnerPhone, validatedPartnerEmail, validatedPartnerPassword).any {
                !it.successful
            }

            if (hasError) {
                addEditState = addEditState.copy(
                    partnerNameError = validatedPartnerName.errorMessage,
                    partnerPhoneError = validatedPartnerPhone.errorMessage,
                    partnerEmailError = validatedPartnerEmail.errorMessage,
                    partnerPasswordError = validatedPartnerPassword.errorMessage,
                )

                return@launch
            }else {
                viewModelScope.launch {
                    if(partnerId.isNullOrEmpty()){
                        val result = partnerUseCases.createNewPartner(
                            DeliveryPartner(
                                deliveryPartnerName = addEditState.partnerName,
                                deliveryPartnerPhone = addEditState.partnerPhone,
                                deliveryPartnerEmail = addEditState.partnerEmail,
                                deliveryPartnerPassword = addEditState.partnerPassword,
                                deliveryPartnerStatus = addEditState.partnerStatus,
                                deliveryPartnerType = addEditState.partnerType,
                            )
                        )
                        when(result){
                            is Resource.Loading -> {
                                _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Partner created successfully"))
                                addEditState = AddEditPartnerState()
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new employee"))
                            }
                        }

                    }else {
                        val result = partnerUseCases.updatePartner(
                            DeliveryPartner(
                                deliveryPartnerName = addEditState.partnerName,
                                deliveryPartnerPhone = addEditState.partnerPhone,
                                deliveryPartnerEmail = addEditState.partnerEmail,
                                deliveryPartnerPassword = addEditState.partnerPassword,
                                deliveryPartnerStatus = addEditState.partnerStatus,
                                deliveryPartnerType = addEditState.partnerType,
                            ),
                            partnerId
                        )
                        when(result){
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError( "Unable to Update Partner"))
                            }
                            is Resource.Loading -> {

                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Partner updated successfully"))
                                addEditState = AddEditPartnerState()
                            }
                        }
                    }
                }
            }
        }

    }

    fun togglePassword(status: Boolean){
        _passwordToggle.value = status
    }

}