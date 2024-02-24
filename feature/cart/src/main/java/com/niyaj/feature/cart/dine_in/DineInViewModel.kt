package com.niyaj.feature.cart.dine_in

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.CartRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DineInViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val cartOrderRepository: CartOrderRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    val dineInOrders = snapshotFlow { searchText.value }
        .flatMapLatest { cartRepository.getAllDineInOrders() }
        .mapLatest { result ->
            totalItems = result.map { it.cartOrderId }

            if (result.isEmpty()) UiState.Empty else UiState.Success(result)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    val addOnItems = snapshotFlow { searchText.value }.flatMapLatest {
        cartRepository.getAllAddOnItems(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onEvent(event: DineInEvent) {
        when (event) {
            is DineInEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    if (event.cartOrderId.isEmpty()) {
                        mEventFlow.emit(UiEvent.Error("Create New Order First"))
                    } else if (event.productId.isEmpty()) {
                        mEventFlow.emit(UiEvent.Error("Unable to get product"))
                    } else {
                        when (val result =
                            cartRepository.addProductToCart(event.cartOrderId, event.productId)) {
                            is Resource.Success -> {
//                                mEventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }

                            is Resource.Error -> {
                                mEventFlow.emit(
                                    UiEvent.Error(
                                        result.message ?: "Error adding product to cart"
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is DineInEvent.DecreaseQuantity -> {
                viewModelScope.launch {
                    when (cartRepository.removeProductFromCart(
                        event.cartOrderId,
                        event.productId
                    )) {
                        is Resource.Success -> {
//                            mEventFlow.emit(UiEvent.OnSuccess("Item removed from cart"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Error removing product from cart"))
                        }
                    }
                }
            }

            is DineInEvent.UpdateAddOnItemInCart -> {
                viewModelScope.launch {
                    when (cartOrderRepository.updateAddOnItem(
                        event.addOnItemId,
                        event.cartOrderId
                    )) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Unable To Update AddOnItem"))
                        }

                        else -> {}
                    }
                }
            }

            is DineInEvent.PlaceDineInOrder -> {
                viewModelScope.launch {
                    when (cartOrderRepository.placeOrder(event.cartOrderId)) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("Order Placed Successfully"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Unable To Place Order"))
                        }
                    }
                }
            }

            is DineInEvent.PlaceAllDineInOrder -> {
                viewModelScope.launch {
                    when (cartOrderRepository.placeAllOrder(mSelectedItems.toList())) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("${mSelectedItems.size} DineIn Order Placed Successfully"))
                            mSelectedItems.clear()
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Unable To Place All DineIn Order"))
                        }
                    }
                }
            }
        }
    }

}