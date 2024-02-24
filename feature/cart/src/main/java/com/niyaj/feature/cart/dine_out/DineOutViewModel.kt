package com.niyaj.feature.cart.dine_out

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
class DineOutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val cartOrderRepository: CartOrderRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    val dineOutOrders = snapshotFlow { searchText.value }
        .flatMapLatest { cartRepository.getAllDineOutOrders() }
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

    fun onEvent(event: DineOutEvent) {
        when (event) {
            is DineOutEvent.IncreaseQuantity -> {
                viewModelScope.launch {
                    if (event.orderId.isEmpty()) {
                        mEventFlow.emit(UiEvent.Error("Create New Order First"))
                    } else if (event.productId.isEmpty()) {
                        mEventFlow.emit(UiEvent.Error("Unable to get product"))
                    } else {
                        when (val result =
                            cartRepository.addProductToCart(event.orderId, event.productId)) {
                            is Resource.Success -> {
//                                mEventFlow.emit(UiEvent.OnSuccess("Item added to cart"))
                            }

                            is Resource.Error -> {
                                mEventFlow.emit(
                                    UiEvent.Error(
                                        result.message
                                            ?: "Error adding product to cart"
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is DineOutEvent.DecreaseQuantity -> {
                viewModelScope.launch {

                    when (cartRepository.removeProductFromCart(event.orderId, event.productId)) {
                        is Resource.Success -> {
//                            mEventFlow.emit(UiEvent.OnSuccess("Item removed from cart"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Error removing product from cart"))
                        }
                    }
                }
            }

            is DineOutEvent.UpdateAddOnItemInCart -> {
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

            is DineOutEvent.PlaceDineOutOrder -> {
                viewModelScope.launch {
                    when (cartOrderRepository.placeOrder(event.cartId)) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("Order Placed Successfully"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Unable To Place Order"))
                        }
                    }
                }
            }

            is DineOutEvent.PlaceAllDineOutOrder -> {
                viewModelScope.launch {
                    when (cartOrderRepository.placeAllOrder(mSelectedItems)) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("${mSelectedItems.size} DineOut Order Placed Successfully"))
                            mSelectedItems.clear()
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.Error("Unable To Place All DineOut Order"))
                        }
                    }
                }
            }
        }
    }
}