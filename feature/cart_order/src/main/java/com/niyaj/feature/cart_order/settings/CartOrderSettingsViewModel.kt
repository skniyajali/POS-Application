package com.niyaj.feature.cart_order.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderSettingsViewModel @Inject constructor(
    private val cartOrderRepository : CartOrderRepository
): ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onEvent(event: CartOrderSettingsEvent) {
        when(event) {
            is CartOrderSettingsEvent.DeleteAllCartOrders -> {
                viewModelScope.launch {
                    when(val result = cartOrderRepository.deleteCartOrders(true)){
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("All cart orders were successfully deleted"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete cart orders"))
                        }
                    }
                }
            }

            is CartOrderSettingsEvent.DeletePastSevenDaysBeforeData -> {
                viewModelScope.launch {
                    when(val result = cartOrderRepository.deleteCartOrders(false)){
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Last 7 Days orders were successfully deleted"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete last 7 days cart orders"))
                        }
                    }
                }
            }
        }
    }
}