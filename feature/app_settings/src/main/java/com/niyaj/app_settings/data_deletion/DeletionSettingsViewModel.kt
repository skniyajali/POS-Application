package com.niyaj.app_settings.data_deletion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.SettingsValidationRepository
import com.niyaj.model.Settings
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeletionSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val validationRepository: SettingsValidationRepository,
) : ViewModel() {

    var state by mutableStateOf(DeletionSettingsState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getSetting()
    }

    val expensesIntervalError = snapshotFlow { state.expensesInterval }.mapLatest {
        validationRepository.validateExpensesInterval(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val reportsIntervalError = snapshotFlow { state.reportsInterval }.mapLatest {
        validationRepository.validateReportsInterval(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val cartIntervalError = snapshotFlow { state.cartInterval }.mapLatest {
        validationRepository.validateCartInterval(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val cartOrderIntervalError = snapshotFlow { state.cartOrderInterval }.mapLatest {
        validationRepository.validateCartOrderInterval(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onEvent(event: DeletionSettingsEvent) {
        when (event) {
            is DeletionSettingsEvent.CartIntervalChanged -> {
                state = state.copy(
                    cartInterval = event.cartInterval
                )
            }

            is DeletionSettingsEvent.CartOrderIntervalChanged -> {
                state = state.copy(
                    cartOrderInterval = event.cartOrderInterval
                )
            }

            is DeletionSettingsEvent.ExpensesIntervalChanged -> {
                state = state.copy(
                    expensesInterval = event.expensesInterval
                )
            }

            is DeletionSettingsEvent.ReportsIntervalChanged -> {
                state = state.copy(
                    reportsInterval = event.reportsInterval
                )
            }

            is DeletionSettingsEvent.UpdateSettings -> {
                updateSetting()
            }
        }
    }

    private fun updateSetting() {
        viewModelScope.launch {
            val settings = Settings(
                expensesDataDeletionInterval = state.expensesInterval.toInt(),
                reportDataDeletionInterval = state.reportsInterval.toInt(),
                cartDataDeletionInterval = state.cartInterval.toInt(),
                cartOrderDataDeletionInterval = state.cartOrderInterval.toInt(),
            )

            when (val result = settingsRepository.updateSetting(settings)) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Deletion Settings Updated"))
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to update settings"))
                }
            }
        }
    }

    private fun getSetting() {
        viewModelScope.launch {
            when (val result = settingsRepository.getSetting()) {
                is Resource.Success -> {
                    result.data?.let { data ->
                        state = state.copy(
                            expensesInterval = data.expensesDataDeletionInterval.toString(),
                            reportsInterval = data.reportDataDeletionInterval.toString(),
                            cartInterval = data.cartDataDeletionInterval.toString(),
                            cartOrderInterval = data.cartOrderDataDeletionInterval.toString(),
                        )
                    }
                }

                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.Error(
                            result.message ?: "Unable to get data deletion settings"
                        )
                    )
                }
            }
        }

    }
}