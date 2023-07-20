package com.niyaj.popos.features.account.presentation.change_password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.account.domain.repository.AccountRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountRepository : AccountRepository,
    validationRepository : RestaurantInfoValidationRepository,
) : ViewModel() {

    var state by mutableStateOf(ChangePasswordState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val passwordError = snapshotFlow { state.newPassword }.mapLatest {
        validationRepository.validatePassword(it).errorMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val confirmPasswordError = snapshotFlow { state.confirmPassword }.mapLatest {
        if (state.newPassword != it) {
            "Current password does not match"
        } else null
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )


    fun onEvent(event : ChangePasswordEvent) {
        when (event) {

            is ChangePasswordEvent.CurrentPasswordChanged -> {
                state = state.copy(
                    currentPassword = event.currentPassword
                )
            }

            is ChangePasswordEvent.NewPasswordChanged -> {
                state = state.copy(
                    newPassword = event.newPassword
                )
            }

            is ChangePasswordEvent.ConfirmPasswordChanged -> {
                state = state.copy(
                    confirmPassword = event.confirmPassword
                )
            }

            is ChangePasswordEvent.ChangePassword -> {
                changePassword()
            }

        }
    }

    private fun changePassword() {
        viewModelScope.launch {

            val hasError = listOf(
                passwordError,
                confirmPasswordError
            ).any { it.value != null }

            if (!hasError) {
                val result = accountRepository.changePassword(
                    currentPassword = state.currentPassword,
                    newPassword = state.newPassword
                )

                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Password changed successfully"))
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                    }
                }
            }else {
                return@launch
            }
        }
    }

}