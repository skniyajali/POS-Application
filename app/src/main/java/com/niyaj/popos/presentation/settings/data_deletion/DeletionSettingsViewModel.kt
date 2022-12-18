package com.niyaj.popos.presentation.settings.data_deletion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.use_cases.app_settings.SettingsUseCases
import com.niyaj.popos.domain.use_cases.app_settings.validation.ValidateCartInterval
import com.niyaj.popos.domain.use_cases.app_settings.validation.ValidateCartOrderInterval
import com.niyaj.popos.domain.use_cases.app_settings.validation.ValidateExpensesInterval
import com.niyaj.popos.domain.use_cases.app_settings.validation.ValidateReportsInterval
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.realm.app_settings.SettingsRealm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeletionSettingsViewModel @Inject constructor(
    private val validateExpensesInterval: ValidateExpensesInterval,
    private val validateReportsInterval: ValidateReportsInterval,
    private val validateCartInterval: ValidateCartInterval,
    private val validateCartOrderInterval: ValidateCartOrderInterval,
    private val settingsUseCases: SettingsUseCases
): ViewModel() {
    
    var state by mutableStateOf(DeletionSettingsState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getSetting()
    }

    fun onEvent(event: DeletionSettingsEvent) {
        when(event) {
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
        val validatedExpenses = validateExpensesInterval.validate(state.expensesInterval)
        val validatedReports = validateReportsInterval.validate(state.reportsInterval)
        val validatedCart = validateCartInterval.validate(state.cartInterval)
        val validatedCartOrder = validateCartOrderInterval.validate(state.cartOrderInterval)

        val hasError = listOf(validatedExpenses, validatedReports, validatedCart, validatedCartOrder).any {
            !it.successful
        }

        if (hasError) {
            state = state.copy(
                expensesIntervalError = validatedExpenses.errorMessage,
                reportsIntervalError = validatedReports.errorMessage,
                cartIntervalError = validatedCart.errorMessage,
                cartOrderIntervalError = validatedCartOrder.errorMessage
            )

            return
        }

        viewModelScope.launch {
            try {
                val settings = SettingsRealm()
                settings.expensesDataDeletionInterval = state.expensesInterval.toInt()
                settings.reportDataDeletionInterval = state.reportsInterval.toInt()
                settings.cartDataDeletionInterval = state.cartInterval.toInt()
                settings.cartOrderDataDeletionInterval = state.cartOrderInterval.toInt()

                when(val result = settingsUseCases.updateSetting(settings)) {
                    is Resource.Loading -> {
                        _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                    }
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Deletion Settings Updated"))
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to deletion settings"))
                    }
                }

            }catch (e: Exception) {
                return@launch _eventFlow.emit(UiEvent.OnError("Unable to update settings"))
            }
        }
    }

    private fun getSetting() {
        viewModelScope.launch {
            when(val result = settingsUseCases.getSetting()) {
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                }
                is Resource.Success -> {
                    val data = result.data!!
                    
                    state = state.copy(
                        expensesInterval = data.expensesDataDeletionInterval.toString(),
                        reportsInterval = data.reportDataDeletionInterval.toString(),
                        cartInterval = data.cartDataDeletionInterval.toString(),
                        cartOrderInterval = data.cartOrderDataDeletionInterval.toString(),
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get data deletion settings"))
                }
            }
        }
        
    }
}