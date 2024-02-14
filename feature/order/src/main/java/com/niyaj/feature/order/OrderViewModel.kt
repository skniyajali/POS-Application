package com.niyaj.feature.order

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.OrderRepository
import com.niyaj.model.OrderStatus
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    var dineInIsEmpty by mutableStateOf(false)

    var dineOutIsEmpty by mutableStateOf(false)

    private val _selectedDate = mutableStateOf("")
    val selectedDate: State<String> = _selectedDate

    val dineInOrders = snapshotFlow { searchText.value }
        .combine(snapshotFlow { _selectedDate.value }) { searchText, date ->
            orderRepository.getDineInOrders(searchText, date)
        }.flatMapLatest { listFlow ->
            listFlow.map {
                dineInIsEmpty = it.isEmpty()
                if (it.isEmpty()) UiState.Empty else UiState.Success(it)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    val dineOutOrders = snapshotFlow { searchText.value }
        .combine(snapshotFlow { _selectedDate.value }) { searchText, date ->
            orderRepository.getDineOutOrders(searchText, date)
        }.flatMapLatest { listFlow ->
            listFlow.map {
                dineOutIsEmpty = it.isEmpty()
                if (it.isEmpty()) UiState.Empty else UiState.Success(it)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    init {
        savedStateHandle.get<String>("selectedDate")?.let {
            _selectedDate.value = it
        }
    }

    fun onOrderEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.MarkedAsProcessing -> {
                viewModelScope.launch {
                    when (val result = orderRepository.updateOrderStatus(
                        event.cartOrderId,
                        OrderStatus.PROCESSING
                    )) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("Order Marked As Processing"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(
                                UiEvent.Error(
                                    result.message ?: "Unable To Mark Order As Processing"
                                )
                            )
                        }
                    }
                }
            }

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    when (val result = orderRepository.deleteOrder(event.cartOrderId)) {
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.Success("Order Has Been Deleted successfully"))
                        }

                        is Resource.Error -> {
                            mEventFlow.emit(
                                UiEvent.Error(
                                    result.message ?: "Unable To Delete Order"
                                )
                            )
                        }
                    }
                }
            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.value = event.date
                }
            }
        }
    }
}