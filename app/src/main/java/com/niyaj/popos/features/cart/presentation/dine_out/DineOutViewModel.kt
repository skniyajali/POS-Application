package com.niyaj.popos.features.cart.presentation.dine_out

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart.domain.use_cases.CartUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DineOutViewModel @Inject constructor(
    private val cartUseCases: CartUseCases,
    private val cartOrderUseCases: CartOrderUseCases,
): ViewModel() {

    private val _dineOutOrders = MutableStateFlow(DineOutState())
    val dineOutOrders = _dineOutOrders.asStateFlow()

    private val _opSelectedCarts = mutableStateListOf<String>()

    private val _selectedDineOutOrder = MutableStateFlow<List<String>>(listOf())
    val selectedDineOutOrder = _selectedDineOutOrder.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    init {
        getAllDineOutOrders()
    }

    fun onDineOutEvent(event: DineOutEvent) {
        when (event) {

            is DineOutEvent.GetAllDineOutOrders -> {
                getAllDineOutOrders()
            }

            is DineOutEvent.AddProductToCart -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (event.orderId.isEmpty()) {
                        _eventFlow.emit(UiEvent.OnError("Create New Order First"))
                    } else if (event.productId.isEmpty()) {
                        _eventFlow.emit(UiEvent.OnError("Unable to get product"))
                    } else {
                        when (val result =
                            cartUseCases.addProductToCart(event.orderId, event.productId)) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
//                                _eventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.OnError(result.message
                                    ?: "Error adding product to cart"))
                            }
                        }
                    }
                }
            }

            is DineOutEvent.RemoveProductFromCart -> {
                viewModelScope.launch(Dispatchers.IO) {

                    when (cartUseCases.removeProductFromCart(event.orderId, event.productId)) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
//                            _eventFlow.emit(UiEvent.OnSuccess("Item removed from cart"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Error removing product from cart"))
                        }
                    }
                }
            }

            is DineOutEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch(Dispatchers.IO) {
                    when (cartOrderUseCases.updateAddOnItemInCart(event.addOnItemId,
                        event.cartOrderId)) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
//                            _eventFlow.emit(UiEvent.OnSuccess("AddOnItem Updated Successfully"))

                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Update AddOnItem"))
                        }
                    }
                }
            }

            is DineOutEvent.SelectAllDineOutOrder -> {

                count += 1

                _dineOutOrders.value.cartItems.map { cart ->
                    if(cart.cartProducts.isNotEmpty()) {
                        if(count % 2 != 0){
                            val cartItem = _opSelectedCarts.contains(cart.cartOrder?.cartOrderId)
                            if(!cartItem){
                                _opSelectedCarts.add(
                                    cart.cartOrder?.cartOrderId!!
                                )
                            }
                        }else{
                            _opSelectedCarts.removeIf {
                                it == cart.cartOrder?.cartOrderId!!
                            }
                        }
                    }
                }

                _selectedDineOutOrder.tryEmit(_opSelectedCarts.toList())
            }

            is DineOutEvent.SelectDineOutOrder -> {
                val doesAlreadySelected = _opSelectedCarts.contains(event.cartId)

                if(!doesAlreadySelected){
                    _opSelectedCarts.add(event.cartId)
                }else{
                    _opSelectedCarts.remove(event.cartId)
                }

                _selectedDineOutOrder.tryEmit(_opSelectedCarts.toList())
            }

            is DineOutEvent.PlaceDineOutOrder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    when(cartOrderUseCases.placeOrder(event.cartId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Order Placed Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Place Order"))
                        }
                    }
                }
            }

            is DineOutEvent.PlaceAllDineOutOrder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val selectedCartItem = _selectedDineOutOrder.value

                    when(cartOrderUseCases.placeAllOrder(selectedCartItem)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${selectedCartItem.size} DineOut Order Placed Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Place All DineOut Order"))
                        }
                    }
                }
            }
            is DineOutEvent.RefreshDineOutOrder -> {
                getAllDineOutOrders()
            }
        }
    }

    private fun getAllDineOutOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            cartUseCases.getAllDineOutOrders().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _dineOutOrders.value = _dineOutOrders.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _dineOutOrders.value = _dineOutOrders.value.copy(
                                cartItems = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _dineOutOrders.value = _dineOutOrders.value.copy(
                            error = result.message ?: "Unable to get cart items"
                        )
                    }
                }

            }
        }
    }

}