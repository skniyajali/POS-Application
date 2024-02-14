package com.niyaj.cart_selected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.SelectedRepository
import com.niyaj.model.CartOrder
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectedViewModel @Inject constructor(
    private val selectedRepository: SelectedRepository,
    private val cartOrderRepository: CartOrderRepository,
): ViewModel(){

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedCartOrder = MutableStateFlow<String?>(null)
    val selectedCartOrder = _selectedCartOrder.asStateFlow()

    private val _cartOrders = MutableStateFlow<UiState<List<CartOrder>>>(UiState.Loading)
    val cartOrders = _cartOrders.asStateFlow()

    init {
        getAllCartOrders()
        getSelectedOrder()
    }

    internal fun deleteCartOrder(cartOrderId: String){
        viewModelScope.launch {
            when(val result = cartOrderRepository.deleteCartOrder(cartOrderId)) {
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

    private fun getAllCartOrders() {
        viewModelScope.launch {
            selectedRepository.getAllProcessingCartOrder().collectLatest { list ->
                if (list.isEmpty()) {
                    _cartOrders.value = UiState.Empty
                }else {
                    _cartOrders.value = UiState.Success(list)
                }
            }
        }
    }

    private fun getSelectedOrder() {
        viewModelScope.launch {
            cartOrderRepository.getSelectedCartOrders().collectLatest { result ->
                result.let {
                    _selectedCartOrder.value = it
                }
            }
        }
    }
}