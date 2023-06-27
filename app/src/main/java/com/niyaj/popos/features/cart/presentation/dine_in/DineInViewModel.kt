package com.niyaj.popos.features.cart.presentation.dine_in

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
class DineInViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val cartOrderRepository: CartOrderRepository,
): ViewModel() {

    private val _dineInOrders = MutableStateFlow(DineInState())
    val dineInOrders = _dineInOrders.asStateFlow()

    private val _selectedDineInOrder = mutableStateListOf<String>()
    val selectedDineInOrder: SnapshotStateList<String> = _selectedDineInOrder

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var count: Int = 0

    init {
        getAllDineInItems()
    }

    fun onEvent(event: DineInEvent) {
        when (event){

            is DineInEvent.GetAllDineInOrders -> {
                getAllDineInItems()
            }

            is DineInEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    if (event.cartOrderId.isEmpty()) {
                        _eventFlow.emit(UiEvent.OnError("Create New Order First"))
                    }else if(event.productId.isEmpty()){
                        _eventFlow.emit(UiEvent.OnError("Unable to get product"))
                    }else {
                        when (val result = cartRepository.addProductToCart(event.cartOrderId, event.productId)){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
//                                _eventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Error adding product to cart"))
                            }
                        }
                    }
                }
            }

            is DineInEvent.DecreaseQuantity -> {
                viewModelScope.launch {

                    when (cartRepository.removeProductFromCart(event.cartOrderId, event.productId)){
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

            is DineInEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when(cartOrderRepository.updateAddOnItem(event.addOnItemId, event.cartOrderId)){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable To Update AddOnItem"))
                        }
                        else -> {}
                    }
                }
            }

            is DineInEvent.SelectAllDineInOrder -> {
                count += 1

                _dineInOrders.value.cartItems.map { cart ->
                    if(cart.cartProducts.isNotEmpty()) {
                        if(count % 2 != 0){
                            val cartItem = _selectedDineInOrder.contains(cart.cartOrderId)
                            if(!cartItem){
                                _selectedDineInOrder.add(
                                    cart.cartOrderId
                                )
                            }
                        }else{
                            _selectedDineInOrder.removeIf {
                                it == cart.cartOrderId
                            }
                        }
                    }
                }
            }

            is DineInEvent.SelectDineInOrder -> {
                val doesAlreadySelected = _selectedDineInOrder.contains(event.cartOrderId)

                if(!doesAlreadySelected){
                    _selectedDineInOrder.add(event.cartOrderId)
                }else{
                    _selectedDineInOrder.remove(event.cartOrderId)
                }
            }

            is DineInEvent.PlaceDineInOrder -> {
                viewModelScope.launch {
                    when(cartOrderRepository.placeOrder(event.cartOrderId)){
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
                    when(cartOrderRepository.placeAllOrder(_selectedDineInOrder)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${_selectedDineInOrder.size} DineIn Order Placed Successfully"))
                            _selectedDineInOrder.clear()
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
            cartRepository.getAllDineInOrders().collect { result ->
                when (result){
                    is Resource.Loading -> {
                        _dineInOrders.value = _dineInOrders.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _dineInOrders.value = _dineInOrders.value.copy(cartItems = it)
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