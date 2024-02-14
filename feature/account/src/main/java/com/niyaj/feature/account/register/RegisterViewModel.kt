package com.niyaj.feature.account.register

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.ImageStorageManager
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.QRCodeScanner
import com.niyaj.data.repository.RestaurantInfoRepository
import com.niyaj.data.repository.validation.RestaurantInfoValidationRepository
import com.niyaj.feature.account.register.components.basic_info.BasicInfoEvent
import com.niyaj.feature.account.register.components.basic_info.BasicInfoState
import com.niyaj.feature.account.register.components.login_info.LoginInfoEvent
import com.niyaj.feature.account.register.components.login_info.LoginInfoState
import com.niyaj.feature.account.register.utils.RegisterScreenPage
import com.niyaj.model.Account
import com.niyaj.model.RestaurantInfo
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.QRCodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModel @Inject constructor(
    private val accountRepository : AccountRepository,
    private val restaurantInfoRepository : RestaurantInfoRepository,
    private val validation : RestaurantInfoValidationRepository,
    private val scanner : QRCodeScanner,
    application : Application,
) : ViewModel() {

    private val _scannedBitmap = MutableStateFlow<Bitmap?>(null)
    val scannedBitmap = _scannedBitmap.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val pageOrder : List<RegisterScreenPage> = listOf(
        RegisterScreenPage.LOGIN_INFO,
        RegisterScreenPage.BASIC_INFO,
    )

    private var pageIndex = 0

    // Screen states

    private val _loginInfoState = mutableStateOf(LoginInfoState())
    val loginInfoState : LoginInfoState
        get() = _loginInfoState.value

    private val _basicInfoState = mutableStateOf(BasicInfoState())
    val basicInfoState : BasicInfoState
        get() = _basicInfoState.value

    private val _registerScreenData = mutableStateOf(createRegisterScreenData())
    val registerScreenState : RegisterScreenState
        get() = _registerScreenData.value


    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled : Boolean
        get() = getIsNextEnabled()

    val emailError = snapshotFlow { _loginInfoState.value.email }.mapLatest {
        validation.validateRestaurantEmail(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val phoneError = snapshotFlow { _loginInfoState.value.phone }.mapLatest {
        validation.validatePrimaryPhone(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val passwordError = snapshotFlow { _loginInfoState.value.password }.mapLatest {
        validation.validatePassword(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val nameError = snapshotFlow { _loginInfoState.value.name }.mapLatest {
        validation.validateRestaurantName(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val secondaryPhoneError = snapshotFlow { _loginInfoState.value.secondaryPhone }.mapLatest {
        validation.validateSecondaryPhone(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val taglineError = snapshotFlow { _basicInfoState.value.tagline }.mapLatest {
        validation.validateRestaurantTagline(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val descError = snapshotFlow { _basicInfoState.value.description }.mapLatest {
        validation.validateRestaurantDesc(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val addressError = snapshotFlow { _basicInfoState.value.address }.mapLatest {
        validation.validateRestaurantAddress(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val paymentQrCodeError = snapshotFlow { _basicInfoState.value.paymentQrCode }.mapLatest {
        validation.validatePaymentQrCode(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val resLogo = snapshotFlow { _loginInfoState.value.logo }.mapLatest {
        if (it.isNotEmpty()) {
            ImageStorageManager.getImageFromInternalStorage(application.applicationContext, it)
        } else null
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )

    val printLogo = snapshotFlow { _basicInfoState.value.printLogo }.mapLatest {
        if (it.isNotEmpty()) {
            ImageStorageManager.getImageFromInternalStorage(application.applicationContext, it)
        } else null
    }.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed(5_000)
    )


    /**
     * Returns true if the ViewModel handled the back press (i.e., it went back one question)
     */
    fun onBackPressed() : Boolean {
        if (pageIndex == 0) {
            return false
        }
        changePage(pageIndex - 1)
        return true
    }

    fun onPreviousPressed() {
        if (pageIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changePage(pageIndex - 1)
    }

    fun onNextPressed() {
        changePage(pageIndex + 1)
    }

    private fun changePage(pageIndex : Int) {
        this.pageIndex = pageIndex
        _isNextEnabled.value = getIsNextEnabled()
        _registerScreenData.value = createRegisterScreenData()
    }

    fun onDonePressed() {
        viewModelScope.launch {
            val resInfo = RestaurantInfo(
                name = _loginInfoState.value.name,
                email = _loginInfoState.value.email,
                primaryPhone = _loginInfoState.value.phone,
                secondaryPhone = _loginInfoState.value.secondaryPhone,
                tagline = _basicInfoState.value.tagline,
                address = _basicInfoState.value.address,
                description = _basicInfoState.value.description,
                logo = _loginInfoState.value.logo,
                printLogo = _basicInfoState.value.printLogo,
                paymentQrCode = _basicInfoState.value.paymentQrCode
            )

            when(restaurantInfoRepository.updateRestaurantInfo(resInfo)) {
                is Resource.Success -> {
                    val result = accountRepository.register(
                        Account(
                            email = _loginInfoState.value.email,
                            phone = _loginInfoState.value.phone,
                            password = _loginInfoState.value.password,
                            isLoggedIn = true,
                        )
                    )

                    when(result) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Your restaurant profile has been created"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable to create restaurant profile"))
                        }
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to create restaurant profile"))
                }
            }

        }
    }

    private fun getIsNextEnabled() : Boolean {
        return when (pageOrder[pageIndex]) {
            RegisterScreenPage.LOGIN_INFO -> listOf(
                nameError.value,
                secondaryPhoneError.value,
                emailError.value,
                passwordError.value,
                phoneError.value
            ).all { it == null }

            RegisterScreenPage.BASIC_INFO -> listOf(
                taglineError.value,
                addressError.value,
                paymentQrCodeError.value,
            ).all { it == null }
        }
    }

    private fun createRegisterScreenData() : RegisterScreenState {
        return RegisterScreenState(
            pageIndex = pageIndex,
            pageCount = pageOrder.size,
            shouldShowPreviousButton = pageIndex > 0,
            shouldShowDoneButton = pageIndex == pageOrder.size - 1,
            screenPage = pageOrder[pageIndex],
        )
    }

    fun onLoginInfoEvent(event : LoginInfoEvent) {
        when (event) {
            is LoginInfoEvent.NameChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    name = event.name.capitalizeWords
                )
            }

            is LoginInfoEvent.SecondaryPhoneChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    secondaryPhone = event.secondaryPhone
                )
            }

            is LoginInfoEvent.EmailChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    email = event.email
                )
            }

            is LoginInfoEvent.PasswordChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    password = event.password
                )
            }

            is LoginInfoEvent.PhoneChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    phone = event.phone
                )
            }

            is LoginInfoEvent.LogoChanged -> {
                _loginInfoState.value = _loginInfoState.value.copy(
                    logo = event.logo
                )
            }
        }
    }

    fun onBasicInfoEvent(event : BasicInfoEvent) {
        when (event) {
            is BasicInfoEvent.TaglineChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    tagline = event.tagline.capitalizeWords
                )
            }

            is BasicInfoEvent.DescriptionChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    description = event.description.capitalizeWords
                )
            }

            is BasicInfoEvent.AddressChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    address = event.address.capitalizeWords
                )
            }

            is BasicInfoEvent.PaymentQRChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    paymentQrCode = event.paymentQrCode
                )
            }

            is BasicInfoEvent.StartScanning -> {
                startScanning()
            }

            is BasicInfoEvent.PrintLogoChanged -> {
                _basicInfoState.value = _basicInfoState.value.copy(
                    printLogo = event.printLogo
                )
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            scanner.startScanning().collectLatest {
                if (!it.isNullOrEmpty()) {
                    _scannedBitmap.value = QRCodeEncoder().encodeBitmap(it)

                    _basicInfoState.value = _basicInfoState.value.copy(
                        paymentQrCode = it
                    )
                }

            }
        }
    }

}
