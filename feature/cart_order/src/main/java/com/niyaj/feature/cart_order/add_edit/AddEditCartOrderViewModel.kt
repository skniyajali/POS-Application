package com.niyaj.feature.cart_order.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.getCapitalWord
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.OrderType
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository,
    private val validationRepository: CartOrderValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val cartOrderId = savedStateHandle.get<String>("cartOrderId") ?: ""

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var state by mutableStateOf(AddEditCartOrderState())

    private val orderType = snapshotFlow { state.orderType }

    private val _newAddress = MutableStateFlow<Address?>(null)
    val newAddress = _newAddress.asStateFlow()

    private val _newCustomer = MutableStateFlow<Customer?>(null)
    val newCustomer = _newCustomer.asStateFlow()

    val orderId = snapshotFlow { cartOrderId }.mapLatest {
        cartOrderRepository.getLastCreatedOrderId(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = "1"
    )

    val addresses = _newAddress.flatMapLatest {
        cartOrderRepository.getAllAddress(it?.addressName ?: "")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val customers = _newCustomer.flatMapLatest {
        cartOrderRepository.getAllCustomers(it?.customerPhone ?: "")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val customerError = orderType.combine(_newCustomer) { orderType, customer ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateCustomerPhone(
                orderType,
                customer?.customerPhone ?: ""
            ).errorMessage
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val addressError = orderType.combine(_newAddress) { orderType, address ->
        if (orderType != OrderType.DineIn) {
            validationRepository.validateCustomerAddress(
                orderType,
                address?.addressName ?: ""
            ).errorMessage
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val orderIdError = orderId.mapLatest {
        validationRepository.validateOrderId(it, cartOrderId).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        savedStateHandle.get<String>("cartOrderId")?.let { cartOrderId ->
            getCartOrderById(cartOrderId)
        }
    }

    fun onEvent(event: AddEditCartOrderEvent) {
        when (event) {
            is AddEditCartOrderEvent.AddressNameChanged -> {
                viewModelScope.launch {
                    val newAddress = Address(
                        addressId = "",
                        addressName = event.addressName.capitalizeWords,
                        shortName = event.addressName.getCapitalWord(),
                    )

                    _newAddress.value = newAddress
                }
            }

            is AddEditCartOrderEvent.AddressChanged -> {
                viewModelScope.launch {
                    _newAddress.value = event.address
                }
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                viewModelScope.launch {
                    val newCustomer = Customer(
                        customerId = "",
                        customerPhone = event.customerPhone
                    )

                    _newCustomer.value = newCustomer
                }
            }

            is AddEditCartOrderEvent.CustomerChanged -> {
                viewModelScope.launch {
                    _newCustomer.value = event.customer
                }
            }

            is AddEditCartOrderEvent.DoesChargesIncluded -> {
                state = state.copy(
                    doesChargesIncluded = !state.doesChargesIncluded
                )
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                state = if (event.orderType == OrderType.DineIn) {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = false
                    )
                } else {
                    state.copy(
                        orderType = event.orderType,
                        doesChargesIncluded = true
                    )
                }
            }

            is AddEditCartOrderEvent.CreateOrUpdateCartOrder -> {
                createOrUpdateCartOrder(cartOrderId)
            }
        }
    }

    private fun createOrUpdateCartOrder(cartOrderId: String) {
        viewModelScope.launch {
            val enableBtn = listOf(
                orderIdError,
                addressError,
                customerError,
            ).all { it.value == null }

            if (enableBtn) {
                val newCartOrder = CartOrder(
                    cartOrderId = cartOrderId,
                    orderId = orderId.value,
                    orderType = state.orderType,
                    customer = _newCustomer.value,
                    address = _newAddress.value,
                    doesChargesIncluded = state.doesChargesIncluded,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (cartOrderId.isEmpty()) null else System.currentTimeMillis().toString()
                )
                val result = cartOrderRepository.createOrUpdateCartOrder(newCartOrder, cartOrderId)
                val message = if (cartOrderId.isEmpty()) "created" else "updated"

                when (result) {
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Order $message successfully"))
                    }

                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.Error(result.message ?: "Unable")
                        )
                    }
                }
            }

            state = AddEditCartOrderState()
        }
    }

    private fun getCartOrderById(cartOrderId: String) {
        viewModelScope.launch {
            when (val result = cartOrderRepository.getCartOrderById(cartOrderId)) {
                is Resource.Success -> {
                    result.data?.let { cartOrder ->
                        _newAddress.value = cartOrder.address ?: Address()
                        _newCustomer.value = cartOrder.customer ?: Customer()

                        state = state.copy(
                            orderType = cartOrder.orderType,
                            doesChargesIncluded = cartOrder.doesChargesIncluded
                        )
                    }
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to get cart order"))
                }
            }
        }
    }

}