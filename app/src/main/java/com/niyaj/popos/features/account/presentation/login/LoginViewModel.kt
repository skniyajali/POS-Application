package com.niyaj.popos.features.account.presentation.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.account.domain.repository.AccountRepository
import com.niyaj.popos.features.app_settings.data.repository.BackupRestoreService
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.common.util.restartApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository : AccountRepository,
    private val backupRestoreService : BackupRestoreService,
): ViewModel() {

    var state by mutableStateOf(LoginState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val isLoggedIn = accountRepository.checkIsLoggedIn()

    fun onEvent(event : LoginEvent) {
        when(event) {
            is LoginEvent.EmailOrPhoneChanged -> {
                state = state.copy(
                    emailOrPhone = event.emailOrPhone
                )
            }

            is LoginEvent.PasswordChanged -> {
                state = state.copy(
                    password = event.password
                )
            }

            is LoginEvent.OnClickLogin -> {
                viewModelScope.launch {
                    if (state.emailOrPhone.isEmpty()) {
                        state = state.copy(
                            emailError = "Email or Phone No Is Required"
                        )

                        return@launch
                    }

                    if (state.password.isEmpty()) {
                        state = state.copy(
                            passwordError = "Password Is Required"
                        )

                        return@launch
                    }

                    val result = accountRepository.login(
                        state.emailOrPhone,
                        state.password
                    )

                    when(result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Login Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to login"))
                        }
                    }
                }
            }

        }
    }

    fun restoreDatabase(context: Context) {
        viewModelScope.launch {
            when(val result = backupRestoreService.restoreDatabase()) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Database restored successfully"))

                    delay(1000)

                    context.restartApplication()
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to restore database"))
                }
            }
        }
    }

}