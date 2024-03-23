package com.niyaj.cart_selected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.SelectedRepository
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedViewModel @Inject constructor(
    private val selectedRepository: SelectedRepository,
    private val cartOrderRepository: CartOrderRepository,
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()

    val selectedCartOrder = selectedRepository.getSelectedCartOrders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val cartOrders = selectedRepository.getAllProcessingCartOrder().mapLatest {
        UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    internal fun deleteCartOrder(cartOrderId: String) {
        viewModelScope.launch {
            when (val result = cartOrderRepository.deleteCartOrder(cartOrderId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete"))
                }

                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.Success("Cart Order deleted successfully"))
                }
            }
        }
    }

    internal fun selectCartOrder(cartOrderId: String) {
        viewModelScope.launch {
            selectedRepository.markOrderAsSelected(cartOrderId)
        }
    }
}