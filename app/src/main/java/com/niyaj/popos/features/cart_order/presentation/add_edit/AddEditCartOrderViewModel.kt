package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.use_cases.AddressUseCases
import com.niyaj.popos.features.address.presentation.AddressState
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateCustomerAddress
import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateCustomerPhone
import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateOrderId
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.use_cases.CustomerUseCases
import com.niyaj.popos.features.customer.presentation.CustomerState
import com.niyaj.popos.util.capitalizeWords
import com.niyaj.popos.util.getAllCapitalizedLetters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddEditCartOrderViewModel @Inject constructor(
    private val validateOrderId: ValidateOrderId,
    private val validateCustomerPhone: ValidateCustomerPhone,
    private val validateCustomerAddress: ValidateCustomerAddress,
    private val cartOrderUseCases: CartOrderUseCases,
    private val customerUseCases: CustomerUseCases,
    private val addressUseCases: AddressUseCases,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    var state by mutableStateOf(AddEditCartOrderState())
        private set

    private val _addresses = MutableStateFlow(AddressState())
    val addresses = _addresses.asStateFlow()

    private val _customers = mutableStateOf(CustomerState())
    val customers: State<CustomerState> = _customers

    var expanded by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("cartOrderId")?.let { cartOrderId ->
            getCartOrder(cartOrderId)
        }

        getAllAddresses()
        getAllCustomers()
    }

    fun onAddEditCartOrderEvent(event: AddEditCartOrderEvent){
        when (event){

            is AddEditCartOrderEvent.OrderIdChanged -> {
                state = state.copy(orderId = event.orderId)
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                state = state.copy(orderType =  event.orderType)
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        customer =  Customer(
                            customerId = event.customerId,
                            customerPhone = event.customerPhone
                        )
                    )
                }
            }

            is AddEditCartOrderEvent.CustomerAddressChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        address = Address(
                            addressId = event.addressId,
                            shortName = getAllCapitalizedLetters(event.customerAddress),
                            addressName = event.customerAddress.capitalizeWords
                        ),
                    )
                }
            }

            is AddEditCartOrderEvent.CreateNewCartOrder -> {
                createOrUpdateCartOrder()
            }

            is AddEditCartOrderEvent.UpdateCartOrder -> {
                createOrUpdateCartOrder(cartOrderId = event.cartOrderId)
            }

            is AddEditCartOrderEvent.OnSearchAddress -> {
                if(event.searchText.isNotEmpty()){
                    getAllAddresses(event.searchText)
                }
            }

            is AddEditCartOrderEvent.OnSearchCustomer -> {
                if (event.searchText.isNotEmpty()){
                    getAllCustomers(event.searchText)
                }
            }

            is AddEditCartOrderEvent.OnClearAddress -> {
                state = state.copy(
                    address = null
                )

                getAllAddresses()
            }

            is AddEditCartOrderEvent.OnClearCustomer -> {
                state = state.copy(
                    customer =  null
                )

                getAllCustomers()
            }

            is AddEditCartOrderEvent.OnUpdateCartOrder -> {
                getCartOrder(event.cartOrderId)
            }

            is AddEditCartOrderEvent.ResetFields -> {
                state = AddEditCartOrderState()
            }

            is AddEditCartOrderEvent.GetAndSetCartOrderId -> {
                getAndSetOrderId()
            }
        }
    }

    private fun createOrUpdateCartOrder(cartOrderId: String? = null) {

        val orderIdResult = validateOrderId.execute(state.orderId)

        val customerPhoneResult = validateCustomerPhone.execute(
            orderType = state.orderType,
            customerPhone = state.customer?.customerPhone ?: ""
        )
        val customerAddressResult = validateCustomerAddress.execute(
            orderType = state.orderType,
            customerAddress = state.address?.addressName ?: "",
        )

        val hasError = listOf(
            orderIdResult,
            customerPhoneResult,
            customerAddressResult
        ).any {
            !it.successful
        }

        if(hasError) {
            state = state.copy(
                orderIdError = orderIdResult.errorMessage,
                customerError = customerPhoneResult.errorMessage,
                addressError = customerAddressResult.errorMessage,
            )
            return
        }

        viewModelScope.launch {
            if(cartOrderId == null){
                val result = cartOrderUseCases.createCardOrder(
                    CartOrder(
                        orderId = state.orderId,
                        orderType = state.orderType,
                        customer = if(state.orderType != CartOrderType.DineIn.orderType) state.customer else null,
                        address = if(state.orderType != CartOrderType.DineIn.orderType) state.address else null,
                    )
                )

                when(result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Order created successfully"))
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new order"))
                    }
                }
            }else {
                val result = cartOrderUseCases.updateCartOrder(
                    CartOrder(
                        orderId = state.orderId,
                        orderType = state.orderType,
                        customer = if(state.orderType != CartOrderType.DineIn.orderType) state.customer else null,
                        address = if(state.orderType != CartOrderType.DineIn.orderType) state.address else null,
                    ),
                    cartOrderId
                )

                when(result){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Order updated successfully"))

                    }
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError( result.message ?: "Unable to update order"))

                    }
                }
            }

            state = AddEditCartOrderState()
        }
    }

    private fun getAllCustomers(searchText: String = "") {
        viewModelScope.launch {
            customerUseCases.getAllCustomers(searchText = searchText).collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        _customers.value = _customers.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _customers.value = _customers.value.copy(
                                customers = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _customers.value = _customers.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getAllAddresses(searchText: String = "") {
        viewModelScope.launch {
            addressUseCases.getAllAddress(searchText = searchText).collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        _addresses.value = _addresses.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _addresses.value = _addresses.value.copy(
                                addresses = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _addresses.value = _addresses.value.copy(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun getAndSetOrderId() {
        val lastOrderId = cartOrderUseCases.getLastCreatedOrderId()

        if (state.orderId.isEmpty()){
            state = state.copy(
                orderId = lastOrderId.inc().toString()
            )
        }

    }

    private fun getCartOrder(cartOrderId: String){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                cartOrderUseCases.getCartOrder(cartOrderId)
            }
            when (result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let {cartOrder ->
                        state = state.copy(
                            orderId = cartOrder.orderId,
                            orderType = cartOrder.orderType,
                            customer = cartOrder.customer,
                            address =  cartOrder.address,
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to get cart order"))
                }
            }
        }
    }

}