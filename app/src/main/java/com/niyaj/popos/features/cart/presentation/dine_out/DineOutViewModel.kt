package com.niyaj.popos.features.cart.presentation.dine_out

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
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
class DineOutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val cartOrderRepository: CartOrderRepository,
): ViewModel() {

    private val _dineOutOrders = MutableStateFlow(DineOutState())
    val dineOutOrders = _dineOutOrders.asStateFlow()

    private val _selectedDineOutOrder = mutableStateListOf<String>()
    val selectedDineOutOrder: SnapshotStateList<String> = _selectedDineOutOrder

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    init {
        getAllDineOutOrders()
    }

    fun onEvent(event: DineOutEvent) {
        when (event) {

            is DineOutEvent.GetAllDineOutOrders -> {
                getAllDineOutOrders()
            }

            is DineOutEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    if (event.orderId.isEmpty()) {
                        _eventFlow.emit(UiEvent.Error("Create New Order First"))
                    } else if (event.productId.isEmpty()) {
                        _eventFlow.emit(UiEvent.Error("Unable to get product"))
                    } else {
                        when (val result =
                            cartRepository.addProductToCart(event.orderId, event.productId)) {
                            is Resource.Loading -> {}
                            is Resource.Success -> {
//                                _eventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(
                                    UiEvent.Error(result.message
                                    ?: "Error adding product to cart"))
                            }
                        }
                    }
                }
            }

            is DineOutEvent.DecreaseQuantity -> {
                viewModelScope.launch {

                    when (cartRepository.removeProductFromCart(event.orderId, event.productId)) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
//                            _eventFlow.emit(UiEvent.OnSuccess("Item removed from cart"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Error removing product from cart"))
                        }
                    }
                }
            }

            is DineOutEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when (cartOrderRepository.updateAddOnItem(event.addOnItemId, event.cartOrderId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Update AddOnItem"))
                        }
                        else -> {}
                    }
                }
            }

            is DineOutEvent.SelectAllDineOutOrder -> {

                count += 1

                _dineOutOrders.value.cartItems.map { cart ->
                    if(cart.cartProducts.isNotEmpty()) {
                        if(count % 2 != 0){
                            val cartItem = _selectedDineOutOrder.contains(cart.cartOrderId)
                            if(!cartItem){
                                _selectedDineOutOrder.add(cart.cartOrderId)
                            }
                        }else{
                            _selectedDineOutOrder.removeIf {
                                it == cart.cartOrderId
                            }
                        }
                    }
                }
            }

            is DineOutEvent.SelectDineOutOrder -> {
                val doesAlreadySelected = _selectedDineOutOrder.contains(event.cartId)

                if(!doesAlreadySelected){
                    _selectedDineOutOrder.add(event.cartId)
                }else{
                    _selectedDineOutOrder.remove(event.cartId)
                }
            }

            is DineOutEvent.PlaceDineOutOrder -> {
                viewModelScope.launch {
                    when(cartOrderRepository.placeOrder(event.cartId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Order Placed Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Place Order"))
                        }
                    }
                }
            }

            is DineOutEvent.PlaceAllDineOutOrder -> {
                viewModelScope.launch {
                    when(cartOrderRepository.placeAllOrder(_selectedDineOutOrder)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("${_selectedDineOutOrder.size} DineOut Order Placed Successfully"))
                            _selectedDineOutOrder.clear()
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Place All DineOut Order"))
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
        viewModelScope.launch {
            cartRepository.getAllDineOutOrders().collect { result ->
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