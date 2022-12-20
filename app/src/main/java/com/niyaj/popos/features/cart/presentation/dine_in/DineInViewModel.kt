package com.niyaj.popos.features.cart.presentation.dine_in

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart.domain.use_cases.CartUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DineInViewModel @Inject constructor(
    private val cartUseCases: CartUseCases,
    private val cartOrderUseCases: CartOrderUseCases,
): ViewModel() {

    private val _dineInOrders = MutableStateFlow(DineInState())
    val dineInOrders = _dineInOrders.asStateFlow()

    private val _opSelectedCarts = mutableStateListOf<String>()

    private val _selectedDineInOrder = MutableStateFlow<List<String>>(listOf())
    val selectedDineInOrder = _selectedDineInOrder.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    init {
        getAllDineInItems()
    }

    fun onDineInEvent(event: DineInEvent) {
        when (event){

            is DineInEvent.GetAllDineInOrders -> {
                getAllDineInItems()
            }

            is DineInEvent.AddProductToCart -> {
                viewModelScope.launch {
                    if (event.cartOrderId.isEmpty()) {
                        _eventFlow.emit(UiEvent.OnError("Create New Order First"))
                    }else if(event.productId.isEmpty()){
                        _eventFlow.emit(UiEvent.OnError("Unable to get product"))
                    }else {
                        when (val result = cartUseCases.addProductToCart(event.cartOrderId, event.productId)){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Error adding product to cart"))
                            }
                        }
                    }
                }
            }

            is DineInEvent.RemoveProductFromCart -> {
                viewModelScope.launch {

                    when (cartUseCases.removeProductFromCart(event.cartOrderId, event.productId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Item removed from cart"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Error removing product from cart"))
                        }
                    }
                }
            }

            is DineInEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when(cartOrderUseCases.updateAddOnItemInCart(event.addOnItemId, event.cartOrderId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("AddOnItem Updated Successfully"))

                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Update AddOnItem"))
                        }
                    }
                }
            }

            is DineInEvent.SelectAllDineInOrder -> {

                count += 1

                _dineInOrders.value.cartItems.map { cart ->
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

                _selectedDineInOrder.tryEmit(_opSelectedCarts.toList())
            }

            is DineInEvent.SelectDineInOrder -> {
                val doesAlreadySelected = _opSelectedCarts.contains(event.cartOrderId)

                if(!doesAlreadySelected){
                    _opSelectedCarts.add(event.cartOrderId)
                }else{
                    _opSelectedCarts.remove(event.cartOrderId)
                }

                _selectedDineInOrder.tryEmit(_opSelectedCarts.toList())
            }

            is DineInEvent.PlaceDineInOrder -> {
                viewModelScope.launch {
                    when(cartOrderUseCases.placeOrder(event.cartOrderId)){
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

            is DineInEvent.PlaceAllDineInOrder -> {
                viewModelScope.launch {
                    val selectedCartItem = _selectedDineInOrder.value

                    when(cartOrderUseCases.placeAllOrder(selectedCartItem)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${selectedCartItem.size} DineIn Order Placed Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Place All DineIn Order"))
                        }
                    }
                }
            }

            is DineInEvent.RefreshDineInOrder -> {
                getAllDineInItems()
            }
        }
    }

    private fun getAllDineInItems() {
        viewModelScope.launch {
            cartUseCases.getAllDineInOrders().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        _dineInOrders.value = _dineInOrders.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {

                            _dineInOrders.value = _dineInOrders.value.copy(
                                cartItems = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _dineInOrders.value = _dineInOrders.value.copy(
                            error = result.message ?: "Unable to get cart items"
                        )
                    }
                }

            }
        }
    }

}