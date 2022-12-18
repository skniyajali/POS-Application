package com.niyaj.popos.presentation.cart_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.use_cases.cart_order.CartOrderUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val cartOrderUseCases: CartOrderUseCases,
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


    init {
        getAllCartOrders()
        getSelectedCartOrder()
    }

    fun onCartOrderEvent(event: CartOrderEvent) {
        when(event){
            is CartOrderEvent.DeleteCartOrder -> {
                viewModelScope.launch {
                    when(cartOrderUseCases.deleteCartOrder(event.cartOrderId)){
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
                    cartOrderUseCases.selectCartOrder(event.cartOrderId)
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

            is CartOrderEvent.DeleteAllCartOrders -> {
                viewModelScope.launch {
                    when(val result = cartOrderUseCases.deleteCartOrders(true)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("All cart orders were successfully deleted"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete cart orders"))
                        }
                    }
                }
            }

            is CartOrderEvent.DeletePastSevenDaysBeforeData -> {
                viewModelScope.launch {
                    when(val result = cartOrderUseCases.deleteCartOrders(false)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Last 7 Days orders were successfully deleted"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete last 7 days cart orders"))
                        }
                    }
                }
            }

            is CartOrderEvent.RefreshCartOrder -> {
                getAllCartOrders()
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

    private fun getAllCartOrders(searchText : String = "") {
        viewModelScope.launch {
            cartOrderUseCases.getAllCartOrders(searchText).collect { result ->
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