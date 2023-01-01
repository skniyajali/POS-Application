package com.niyaj.popos.features.cart_order.presentation.add_edit

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

    var addEditCartOrderState by mutableStateOf(AddEditCartOrderState())

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

    fun onAddEditCartOrderEvent(event: AddEditCartOrderEvent){
        when (event){

            is AddEditCartOrderEvent.OrderIdChanged -> {
                addEditCartOrderState = addEditCartOrderState.copy(orderId = event.orderId)
            }

            is AddEditCartOrderEvent.OrderTypeChanged -> {
                addEditCartOrderState = addEditCartOrderState.copy(orderType =  event.orderType)
            }

            is AddEditCartOrderEvent.CustomerPhoneChanged -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if(event.customerId != null){
                        val customer = customerUseCases.getCustomerById(event.customerId).data

                        if (customer != null) {
                            addEditCartOrderState = addEditCartOrderState.copy(customer =  customer)
                        }

                    }else {
                        getAllCustomers(event.customerPhone)

                        addEditCartOrderState = addEditCartOrderState.copy(
                            customer =  Customer(
                                customerPhone = event.customerPhone
                            )
                        )
                    }
                }
            }

            is AddEditCartOrderEvent.CustomerAddressChanged -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if(event.addressId != null) {
                        val address = addressUseCases.getAddressById(event.addressId).data
                        if (address != null) {
                            addEditCartOrderState = addEditCartOrderState.copy(address = address)
                        }

                    }else {
                        getAllAddresses(event.customerAddress)

                        addEditCartOrderState = addEditCartOrderState.copy(
                            address = Address(
                                shortName = getAllCapitalizedLetters(event.customerAddress),
                                addressName = event.customerAddress.capitalizeWords
                            ),
                        )
                    }
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
                addEditCartOrderState = addEditCartOrderState.copy(
                    address = null
                )

                getAllAddresses()
            }

            is AddEditCartOrderEvent.OnClearCustomer -> {
                addEditCartOrderState = addEditCartOrderState.copy(
                    customer =  null
                )

                getAllCustomers()
            }

            is AddEditCartOrderEvent.OnUpdateCartOrder -> {
                getCartOrder(event.cartOrderId)
            }

            is AddEditCartOrderEvent.ResetFields -> {
                addEditCartOrderState = AddEditCartOrderState()
            }

            is AddEditCartOrderEvent.GetAndSetCartOrderId -> {
                getAndSetOrderId()
            }
        }
    }

    private fun createOrUpdateCartOrder(cartOrderId: String? = null) {

        val orderIdResult = validateOrderId.execute(addEditCartOrderState.orderId)

        val customerPhoneResult = validateCustomerPhone.execute(
            orderType = addEditCartOrderState.orderType,
            customerPhone = addEditCartOrderState.customer?.customerPhone ?: ""
        )
        val customerAddressResult = validateCustomerAddress.execute(
            orderType = addEditCartOrderState.orderType,
            customerAddress = addEditCartOrderState.address?.addressName ?: "",
        )

        val hasError = listOf(
            orderIdResult,
            customerPhoneResult,
            customerAddressResult
        ).any {
            !it.successful
        }

        if(hasError) {
            addEditCartOrderState = addEditCartOrderState.copy(
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
                        orderId = addEditCartOrderState.orderId,
                        orderType = addEditCartOrderState.orderType,
                        customer = if(addEditCartOrderState.orderType != CartOrderType.DineIn.orderType) addEditCartOrderState.customer else null,
                        address = if(addEditCartOrderState.orderType != CartOrderType.DineIn.orderType) addEditCartOrderState.address else null,
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
                        orderId = addEditCartOrderState.orderId,
                        orderType = addEditCartOrderState.orderType,
                        customer = if(addEditCartOrderState.orderType != CartOrderType.DineIn.orderType) addEditCartOrderState.customer else null,
                        address = if(addEditCartOrderState.orderType != CartOrderType.DineIn.orderType) addEditCartOrderState.address else null,
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

            addEditCartOrderState = AddEditCartOrderState()
        }
    }

    private fun getAllCustomers(searchText: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            customerUseCases.getAllCustomers(searchText = searchText).collectLatest { result ->
                when (result){
                    is Resource.Loading -> {
                        _customers.value = customers.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _customers.value = customers.value.copy(
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
        viewModelScope.launch(Dispatchers.IO) {
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

        if (addEditCartOrderState.orderId.isEmpty()){
            addEditCartOrderState = addEditCartOrderState.copy(
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
                        addEditCartOrderState = addEditCartOrderState.copy(
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