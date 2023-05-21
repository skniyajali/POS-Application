package com.niyaj.popos.features.customer.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    private val customerRepository : CustomerRepository,
    savedStateHandle : SavedStateHandle
): ViewModel() {

    private val _customerDetails = MutableStateFlow(Customer())
    val customerDetails = _customerDetails.asStateFlow()

    private val _orderDetails = MutableStateFlow(CustomerDetailsState())
    val orderDetails = _orderDetails.asStateFlow()

    private val _totalOrders = MutableStateFlow(TotalCustomerOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("customerId")?.let { customerId ->
            getCustomerById(customerId)
            getCustomerOrderDetails(customerId)
        }
    }


    private fun getCustomerById(customerId: String) {
        viewModelScope.launch {
            when (val result = customerRepository.getCustomerById(customerId)){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let { customer ->
                        _customerDetails.value = customer
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get customer"))
                }
            }
        }
    }

    private fun getCustomerOrderDetails(customerId: String) {
        viewModelScope.launch {
            customerRepository.getCustomerWiseOrder(customerId).collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        _orderDetails.value = _orderDetails.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {orders ->
                            _orderDetails.value = _orderDetails.value.copy(orderDetails = orders)

                            val startDate = if (orders.isNotEmpty()) orders.first().updatedAt else ""
                            val endDate = if (orders.isNotEmpty()) orders.last().updatedAt else ""
                            val repeatedOrder = orders.groupingBy { it.customerAddress }
                                .eachCount()
                                .filter { it.value > 1 }.size

                            _totalOrders.value = _totalOrders.value.copy(
                                totalAmount = orders.sumOf { it.totalPrice.toLong() }.toString(),
                                totalOrder = orders.size,
                                repeatedOrder = repeatedOrder,
                                datePeriod = Pair(startDate, endDate)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _orderDetails.value = _orderDetails.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

}