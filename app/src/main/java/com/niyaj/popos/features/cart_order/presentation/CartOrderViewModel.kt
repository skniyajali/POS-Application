package com.niyaj.popos.features.cart_order.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val cartOrderUseCases: CartOrderUseCases,
    private val cartOrderRepository: CartOrderRepository
): ViewModel() {

    private val _cartOrders = MutableStateFlow(CartOrderState())
    val cartOrders = _cartOrders.asStateFlow()

    private val _selectedCartOrder = MutableStateFlow(CartOrder())
    val selectedCartOrder =  _selectedCartOrder.asStateFlow()

    private val _selectedOrder = MutableStateFlow("")
    val selectedOrder = _selectedOrder.asStateFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _viewAll = mutableStateOf(false)

    init {
        getAllCartOrders()
        getSelectedCartOrder()
    }

    fun onEvent(event: CartOrderEvent) {
        when(event){
            is CartOrderEvent.DeleteCartOrder -> {
                viewModelScope.launch {
                    when(cartOrderRepository.deleteCartOrder(event.cartOrderId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _selectedOrder.value = ""
                            getSelectedCartOrder()
                            _eventFlow.emit(UiEvent.OnSuccess("CartOrder Deleted Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to delete CartOrder"))
                        }
                    }
                }
            }

            is CartOrderEvent.SelectCartOrderEvent -> {
                viewModelScope.launch {
                    cartOrderRepository.addSelectedCartOrder(event.cartOrderId)
                    _selectedOrder.emit("")
                }
            }

            is CartOrderEvent.SelectCartOrder -> {
                viewModelScope.launch {
                    if(_selectedOrder.value.isNotEmpty() && _selectedOrder.value == event.cartOrderId){
                        _selectedOrder.emit("")
                    }else{
                        _selectedOrder.emit(event.cartOrderId)
                    }
                }
            }

            is CartOrderEvent.OnSearchCartOrder -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllCartOrders(event.searchText)
                }
            }

            is CartOrderEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is CartOrderEvent.RefreshCartOrder -> {
                getAllCartOrders()
            }

            is CartOrderEvent.ViewAllOrders -> {
                _viewAll.value = true
                getAllCartOrders(viewAll = true)
            }
        }
    }

    private fun getSelectedCartOrder(){
        viewModelScope.launch {
            cartOrderUseCases.getSelectedCartOrder().collectLatest { result ->
                if(result != null){
                    _selectedCartOrder.value = result
                }else{
                    _selectedCartOrder.value = CartOrder()
                }
            }
        }
    }

    private fun getAllCartOrders(searchText : String = "", viewAll: Boolean = _viewAll.value) {
        viewModelScope.launch {
            cartOrderUseCases.getAllCartOrders(searchText, viewAll).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _cartOrders.value = _cartOrders.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let { cartOrders ->
                            _cartOrders.value = _cartOrders.value.copy(
                                cartOrders = cartOrders
                            )
                        }
                    }
                    is Resource.Error -> {
                        _cartOrders.value = _cartOrders.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllCartOrders()
        }
    }

}