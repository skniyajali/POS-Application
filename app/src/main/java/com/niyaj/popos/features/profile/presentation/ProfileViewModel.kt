package com.niyaj.popos.features.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.profile.domain.use_cases.RestaurantInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val restaurantInfoUseCases: RestaurantInfoUseCases
) : ViewModel() {

    var updateState by mutableStateOf(UpdateProfileState())

    var isLoading by mutableStateOf(false)

    private val _info = MutableStateFlow(RestaurantInfo())
    val info = _info.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getProfileInfo()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.NameChanged -> {
                updateState = updateState.copy(
                    name = event.name
                )
            }

            is ProfileEvent.TaglineChanged -> {
                updateState = updateState.copy(
                    tagline = event.tagline
                )
            }

            is ProfileEvent.PrimaryPhoneChanged -> {
                updateState = updateState.copy(
                    primaryPhone = event.primaryPhone
                )
            }

            is ProfileEvent.SecondaryPhoneChanged -> {
                updateState = updateState.copy(
                    secondaryPhone = event.secondaryPhone
                )
            }

            is ProfileEvent.EmailChanged -> {
                updateState = updateState.copy(
                    email = event.email
                )
            }

            is ProfileEvent.DescriptionChanged -> {
                updateState = updateState.copy(
                    description = event.description
                )
            }

            is ProfileEvent.AddressChanged -> {
                updateState = updateState.copy(
                    address = event.address
                )
            }

            is ProfileEvent.LogoChanged -> {
                updateState = updateState.copy(
                    logo = event.logo
                )
            }

            is ProfileEvent.PaymentQrCodeChanged -> {
                updateState = updateState.copy(
                    paymentQrCode = event.paymentQrCode
                )
            }

            is ProfileEvent.UpdateProfile -> {
                updateProfile()
            }

            is ProfileEvent.RefreshEvent -> {
                getProfileInfo()
            }

            is ProfileEvent.SetProfileInfo -> {
                setProfileInfo()
            }
        }
    }

    private fun updateProfile() {
        val validatedName = restaurantInfoUseCases.validateRestaurantName(updateState.name)
        val validatedTagLine = restaurantInfoUseCases.validateRestaurantTagline(updateState.tagline)
        val validatedEmail = restaurantInfoUseCases.validateRestaurantEmail(updateState.email)
        val validatedPrimaryPhone = restaurantInfoUseCases.validatePrimaryPhone(updateState.primaryPhone)
        val validatedSecondaryPhone = restaurantInfoUseCases.validateSecondaryPhone(updateState.secondaryPhone)
        val validatedAddress = restaurantInfoUseCases.validateRestaurantAddress(updateState.address)
        val validatedPaymentQrCode = restaurantInfoUseCases.validatePaymentQrCode(updateState.paymentQrCode)


        val hasError = listOf(
            validatedName,
            validatedTagLine,
            validatedEmail,
            validatedPrimaryPhone,
            validatedSecondaryPhone,
            validatedAddress,
            validatedPaymentQrCode
        ).any { !it.successful }

        if (hasError) {
            updateState = updateState.copy(
                nameError = validatedName.errorMessage,
                taglineError = validatedTagLine.errorMessage,
                emailError = validatedEmail.errorMessage,
                primaryPhoneError = validatedPrimaryPhone.errorMessage,
                secondaryPhoneError = validatedSecondaryPhone.errorMessage,
                addressError = validatedAddress.errorMessage,
                paymentQrCodeError = validatedPaymentQrCode.errorMessage,
            )

            return
        } else {
            viewModelScope.launch {
                val result = restaurantInfoUseCases.updateRestaurantInfo(
                    RestaurantInfo(
                        name = updateState.name,
                        tagline = updateState.tagline,
                        email = updateState.email,
                        primaryPhone = updateState.primaryPhone,
                        secondaryPhone = updateState.secondaryPhone,
                        description = updateState.description,
                        address = updateState.address,
                        paymentQrCode = updateState.paymentQrCode,
                        logo = updateState.logo,
                    )
                )

                when(result){
                    is Resource.Loading -> {
                        _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Restaurant Info Updated."))
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to update restaurant info."))
                    }
                }
            }
        }
    }

    private fun getProfileInfo() {
        viewModelScope.launch {
            isLoading = true
            when (val result = restaurantInfoUseCases.getRestaurantInfo()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    result.data?.let {
                        _info.value = it
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get restaurant info"))
                }
            }
            isLoading = false
        }
    }

    private fun setProfileInfo() {
        viewModelScope.launch {
            when (val result = restaurantInfoUseCases.getRestaurantInfo()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    result.data?.let { info ->
                        updateState = updateState.copy(
                            name = info.name,
                            tagline = info.tagline,
                            email = info.email,
                            primaryPhone = info.primaryPhone,
                            secondaryPhone = info.secondaryPhone,
                            description = info.description,
                            address = info.address,
                            paymentQrCode = info.paymentQrCode,
                            logo = info.logo,
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get restaurant info"))
                }
            }
        }
    }
}