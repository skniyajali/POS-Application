package com.niyaj.feature.profile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.QRCodeScanner
import com.niyaj.data.repository.RestaurantInfoRepository
import com.niyaj.data.repository.validation.RestaurantInfoValidationRepository
import com.niyaj.feature.profile.add_edit.UpdateProfileState
import com.niyaj.model.Account
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.model.RestaurantInfo
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.QRCodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository : RestaurantInfoRepository,
    private val validation : RestaurantInfoValidationRepository,
    private val accountRepository : AccountRepository,
    private val scanner : QRCodeScanner
) : ViewModel() {

    var updateState by mutableStateOf(UpdateProfileState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    val info = repository.getRestaurantInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RestaurantInfo()
    )

    val accountInfo = accountRepository.getAccountInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Account()
    )


    fun onEvent(event : ProfileEvent) {
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

            is ProfileEvent.PaymentQrCodeChanged -> {
                updateState = updateState.copy(
                    paymentQrCode = event.paymentQrCode
                )
            }

            is ProfileEvent.UpdateProfile -> {
                updateProfile()
            }

            is ProfileEvent.SetProfileInfo -> {
                setProfileInfo()
            }

            is ProfileEvent.LogoChanged -> {
                viewModelScope.launch {
                    when (repository.updateRestaurantLogo(RESTAURANT_LOGO_NAME)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Profile photo has been updated"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable to update profile photo"))
                        }
                    }
                }
            }

            is ProfileEvent.PrintLogoChanged -> {
                viewModelScope.launch {
                    when (repository.updatePrintLogo(RESTAURANT_PRINT_LOGO_NAME)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Print photo has been updated"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable to update print photo"))
                        }
                    }
                }
            }

            is ProfileEvent.StartScanning -> {
                startScanning()
            }

            is ProfileEvent.LogoutProfile -> {
                viewModelScope.launch {
                    accountRepository.logOut()
                }
            }
        }
    }

    private fun updateProfile() {
        val validatedName = validation.validateRestaurantName(updateState.name)
        val validatedTagLine = validation.validateRestaurantTagline(updateState.tagline)
        val validatedEmail = validation.validateRestaurantEmail(updateState.email)
        val validatedPPhone = validation.validatePrimaryPhone(updateState.primaryPhone)
        val validatedSPhone = validation.validateSecondaryPhone(updateState.secondaryPhone)
        val validatedAddress = validation.validateRestaurantAddress(updateState.address)
        val validatedQrCode = validation.validatePaymentQrCode(updateState.paymentQrCode)
        val validatedDesc = validation.validatePaymentQrCode(updateState.description)

        val hasError = listOf(
            validatedName,
            validatedTagLine,
            validatedEmail,
            validatedPPhone,
            validatedSPhone,
            validatedAddress,
            validatedDesc,
            validatedQrCode
        ).any { !it.successful }

        if (hasError) {
            updateState = updateState.copy(
                nameError = validatedName.errorMessage,
                taglineError = validatedTagLine.errorMessage,
                emailError = validatedEmail.errorMessage,
                primaryPhoneError = validatedPPhone.errorMessage,
                secondaryPhoneError = validatedSPhone.errorMessage,
                addressError = validatedAddress.errorMessage,
                paymentQrCodeError = validatedQrCode.errorMessage,
                descriptionError = validatedDesc.errorMessage
            )

            return
        } else {
            viewModelScope.launch {
                val result = repository.updateRestaurantInfo(
                    RestaurantInfo(
                        name = updateState.name,
                        tagline = updateState.tagline,
                        email = updateState.email,
                        primaryPhone = updateState.primaryPhone,
                        secondaryPhone = updateState.secondaryPhone,
                        description = updateState.description,
                        address = updateState.address,
                        paymentQrCode = updateState.paymentQrCode,
                    )
                )

                when (result) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Restaurant Info Updated."))
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(
                                result.message ?: "Unable to update restaurant info."
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setProfileInfo() {
        viewModelScope.launch {
            repository.getRestaurantInfo().collectLatest { info ->
                if (info.paymentQrCode.isNotEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(info.paymentQrCode)
                }

                updateState = updateState.copy(
                    name = info.name,
                    tagline = info.tagline,
                    email = info.email,
                    primaryPhone = info.primaryPhone,
                    secondaryPhone = info.secondaryPhone,
                    description = info.description,
                    address = info.address,
                    paymentQrCode = info.paymentQrCode,
                )
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScanning().collectLatest {
                if (!it.isNullOrEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)

                    updateState = updateState.copy(
                        paymentQrCode = it
                    )
                }

            }
        }
    }
}