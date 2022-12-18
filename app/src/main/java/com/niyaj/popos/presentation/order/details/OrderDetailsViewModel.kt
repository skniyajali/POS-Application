package com.niyaj.popos.presentation.order.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.use_cases.order.OrderUseCases
import com.niyaj.popos.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _orderDetails = MutableStateFlow(OrderDetailState())
    val orderDetails = _orderDetails.asStateFlow()

    init {
        savedStateHandle.get<String>("cartOrderId")?.let { cartOrderId ->
            getOrderDetails(cartOrderId)
        }
    }

    private fun getOrderDetails(cartOrderId: String) {
        viewModelScope.launch {
            when(val result = orderUseCases.getOrderDetails(cartOrderId)){
                is Resource.Loading -> {
                    _orderDetails.value = _orderDetails.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let {
                        _orderDetails.value = _orderDetails.value.copy(
                            orderDetails = it
                        )
                    }
                }
                is Resource.Error -> {
                    _orderDetails.value = _orderDetails.value.copy(
                        error = result.message ?: "Unable to find order details"
                    )
                }
            }
        }
    }

}