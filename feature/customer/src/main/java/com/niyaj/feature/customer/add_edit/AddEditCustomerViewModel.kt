package com.niyaj.feature.customer.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.model.Customer
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val validationRepository: CustomerValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var customerId = savedStateHandle.get<String>("customerId") ?: ""

    var addEditState by mutableStateOf(AddEditCustomerState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("customerId")?.let { customerId ->
            getCustomerById(customerId)
        }
    }

    val phoneError: StateFlow<String?> = snapshotFlow { addEditState.customerPhone }
        .mapLatest {
            validationRepository.validateCustomerPhone(it, customerId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.customerName }
        .mapLatest {
            validationRepository.validateCustomerName(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val emailError: StateFlow<String?> = snapshotFlow { addEditState.customerEmail }
        .mapLatest {
            validationRepository.validateCustomerEmail(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditCustomerEvent) {
        when (event) {
            is AddEditCustomerEvent.CustomerNameChanged -> {
                addEditState = addEditState.copy(customerName = event.customerName)
            }

            is AddEditCustomerEvent.CustomerPhoneChanged -> {
                addEditState = addEditState.copy(customerPhone = event.customerPhone)

            }

            is AddEditCustomerEvent.CustomerEmailChanged -> {
                addEditState = addEditState.copy(customerEmail = event.customerEmail)
            }

            is AddEditCustomerEvent.CreateOrUpdateCustomer -> {
                createOrUpdateCustomer()
            }
        }
    }

    private fun getCustomerById(customerId: String) {
        if (customerId.isNotEmpty()) {
            viewModelScope.launch {
                when (val result = customerRepository.getCustomerById(customerId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error("Unable to retrieve customer"))
                    }

                    is Resource.Success -> {
                        result.data?.let { customer ->
                            addEditState = addEditState.copy(
                                customerPhone = customer.customerPhone,
                                customerName = customer.customerName,
                                customerEmail = customer.customerEmail
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createOrUpdateCustomer(customerId: String = "") {
        viewModelScope.launch {
            if (phoneError.value == null && nameError.value == null && emailError.value == null) {
                val newCustomer = Customer(
                    customerId = customerId,
                    customerPhone = addEditState.customerPhone.trimEnd(),
                    customerName = addEditState.customerName?.trimEnd()?.capitalizeWords,
                    customerEmail = addEditState.customerEmail?.trimEnd(),
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (customerId.isNotEmpty()) System.currentTimeMillis()
                        .toString() else null
                )

                if (customerId.isEmpty()) {
                    when (customerRepository.createNewCustomer(newCustomer)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Create Customer."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Customer Created Successfully."))
                        }
                    }
                } else {
                    when (customerRepository.updateCustomer(newCustomer, customerId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Create Customer."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Customer Updated Successfully."))
                        }
                    }
                }
                addEditState = AddEditCustomerState()
            }
        }
    }
}