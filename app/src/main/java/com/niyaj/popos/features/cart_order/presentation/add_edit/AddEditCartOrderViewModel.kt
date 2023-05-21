package com.niyaj.popos.features.cart_order.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.use_cases.GetAllAddress
import com.niyaj.popos.features.address.presentation.AddressState
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.use_cases.GetAllCustomers
import com.niyaj.popos.features.customer.presentation.CustomerState
import com.niyaj.popos.utils.capitalizeWords
import com.niyaj.popos.utils.getAllCapitalizedLetters
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
    private val cartOrderRepository: CartOrderRepository,
    private val getAllAddress: GetAllAddress,
    private val getAllCustomers : GetAllCustomers,
    private val validationRepository : CartOrderValidationRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    var state by mutableStateOf(AddEditCartOrderState())
        private set

    private val _addresses = MutableStateFlow(AddressState())
    val addresses = _addresses.asStateFlow()

    private val _customers = MutableStateFlow(CustomerState())
    val customers = _customers.asStateFlow()

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

    fun onEvent(event: AddEditCartOrderEvent){
        when (event){

            is AddEditCartOrderEvent.OrderIdChanged -> {
                state = state.copy(orderId = event.orderId)
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                state = state.copy(orderType =  event.orderType)
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                state = state.copy(
                    customer =  Customer(
                        customerId = event.customerId,
                        customerPhone = event.customerPhone
                    )
                )
            }

            is AddEditCartOrderEvent.CustomerAddressChanged -> {
                state = state.copy(
                    address = Address(
                        addressId = event.addressId,
                        shortName = getAllCapitalizedLetters(event.customerAddress),
                        addressName = event.customerAddress.capitalizeWords
                    ),
                )
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

        val orderIdResult = validationRepository.validateOrderId(state.orderId)

        val customerPhoneResult = validationRepository.validateCustomerPhone(
            orderType = state.orderType,
            customerPhone = state.customer?.customerPhone ?: ""
        )
        val customerAddressResult = validationRepository.validateCustomerAddress(
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
                val result = cartOrderRepository.createNewOrder(
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
                val result = cartOrderRepository.updateCartOrder(
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
            getAllCustomers.invoke(searchText = searchText).collectLatest { result ->
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
            getAllAddress.invoke(searchText = searchText).collectLatest { result ->
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
        val lastOrderId = cartOrderRepository.getLastCreatedOrderId()

        if (state.orderId.isEmpty()){
            state = state.copy(
                orderId = lastOrderId.inc().toString()
            )
        }

    }

    private fun getCartOrder(cartOrderId: String){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                cartOrderRepository.getCartOrderById(cartOrderId)
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