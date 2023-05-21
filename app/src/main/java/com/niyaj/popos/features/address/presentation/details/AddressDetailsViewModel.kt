package com.niyaj.popos.features.address.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressDetailsViewModel @Inject constructor(
    private val addressRepository : AddressRepository,
    savedStateHandle : SavedStateHandle
): ViewModel() {

    private val _orderDetails = MutableStateFlow(AddressDetailsState())
    val orderDetails = _orderDetails.asStateFlow()

    private val _addressDetails = MutableStateFlow(Address())
    val addressDetails = _addressDetails.asStateFlow()

    private val _totalOrders = MutableStateFlow(TotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("addressId")?.let { addressId ->
                getAddressDetails(addressId)
                getOrderDetails(addressId)
            }
        }
    }


    private fun getOrderDetails(addressId : String) {
        viewModelScope.launch {
            addressRepository.getRecentOrdersOnAddress(addressId).collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        _orderDetails.value = _orderDetails.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let { orders ->
                            _orderDetails.value = _orderDetails.value.copy(orderDetails = orders)
                            
                            val startDate = if (orders.isNotEmpty()) orders.first().updatedAt else ""
                            val endDate = if (orders.isNotEmpty()) orders.last().updatedAt else ""
                            val repeatedCustomer = orders.groupingBy { it.customerPhone }
                                .eachCount()
                                .filter { it.value > 1 }.size

                            _totalOrders.value = _totalOrders.value.copy(
                                totalAmount = orders.sumOf { it.totalPrice.toLong() }.toString(),
                                totalOrder = orders.size,
                                repeatedCustomer = repeatedCustomer,
                                datePeriod = Pair(startDate, endDate)
                            )
                        }
                    }
                    is Resource.Error -> {
                        _orderDetails.value = _orderDetails.value.copy(error = result.message)
                    }
                }
            }
        }
    }

    private fun getAddressDetails(addressId : String) {
        viewModelScope.launch {
            when(val result = addressRepository.getAddressById(addressId)) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    result.data?.let { address ->
                        _addressDetails.value = address
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get address details"))
                }
            }
        }
    }

}