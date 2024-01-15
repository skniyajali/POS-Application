package com.niyaj.popos.features.customer.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.capitalizeWords
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import com.niyaj.popos.features.customer.domain.use_cases.GetAllCustomers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Customer View Model
 * @author Sk Niyaj Ali
 *
 */
@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val validationRepository : CustomerValidationRepository,
    private val getAllCustomers : GetAllCustomers,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var addEditCustomerState by mutableStateOf(AddEditCustomerState())

    private val _customers = MutableStateFlow(CustomerState())
    val customers = _customers.asStateFlow()

    private val _selectedCustomer  =  mutableStateListOf<String>()
    val selectedCustomer: SnapshotStateList<String> = _selectedCustomer

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    var expanded by mutableStateOf(false)

    private var count: Int = 0

    init {
        getAllCustomers()

        savedStateHandle.get<String>("customerId")?.let { customerId ->
            getCustomerById(customerId)
        }
    }

    fun onCustomerEvent(event: CustomerEvent) {
        when (event){
            is CustomerEvent.CustomerNameChanged -> {
                addEditCustomerState = addEditCustomerState.copy(
                    customerName = event.customerName.capitalizeWords,
                )
            }

            is CustomerEvent.CustomerPhoneChanged -> {
                addEditCustomerState = addEditCustomerState.copy(
                    customerPhone = event.customerPhone,
                )
            }

            is CustomerEvent.CustomerEmailChanged -> {
                addEditCustomerState = addEditCustomerState.copy(
                    customerEmail = event.customerEmail,
                )
            }

            is CustomerEvent.SelectCustomer -> {
                viewModelScope.launch {
                    if(_selectedCustomer.isNotEmpty() && _selectedCustomer.contains(event.customerId)){
                        _selectedCustomer.remove(event.customerId)
                    }else{
                        _selectedCustomer.add(event.customerId)
                    }
                }
            }

            is CustomerEvent.DeselectAllCustomer -> {
                _selectedCustomer.clear()
            }

            is CustomerEvent.SelectAllCustomer -> {
                count += 1

                val customerList = _customers.value.customers

                if (customerList.isNotEmpty()){
                    customerList.forEach { customer ->
                        if (count % 2 != 0){
                            val selectedProduct = _selectedCustomer.contains(customer.customerId)

                            if (!selectedProduct){
                                _selectedCustomer.add(customer.customerId)
                            }
                        }else {
                            _selectedCustomer.remove(customer.customerId)
                        }
                    }
                }
            }

            is CustomerEvent.CreateNewCustomer -> {
                addOrEditCustomer()
            }

            is CustomerEvent.UpdateCustomer -> {
                addOrEditCustomer(event.customerId)
            }

            is CustomerEvent.DeleteCustomer -> {
                if (event.customers.isNotEmpty()){
                    viewModelScope.launch {
                        event.customers.forEach { customer ->

                            when (val result = customerRepository.deleteCustomer(customer)) {
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    _eventFlow.emit(UiEvent.Success("Customer deleted successfully"))
                                    _selectedCustomer.remove(customer)
                                }
                                is Resource.Error -> {
                                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete customer"))
                                }
                            }
                        }

                    }
                }

            }

            is CustomerEvent.OnSearchCustomer -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllCustomers(event.searchText)
                }
            }

            is CustomerEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is CustomerEvent.RefreshCustomer -> {
                getAllCustomers()
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllCustomers(_searchText.value)
        }
    }

    private fun getCustomerById(customerId: String) {
        viewModelScope.launch {
            when (val result = customerRepository.getCustomerById(customerId)){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let { customer ->
                        addEditCustomerState = addEditCustomerState.copy(
                            customerName = customer.customerName,
                            customerPhone = customer.customerPhone,
                            customerEmail = customer.customerEmail,
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to get customer"))
                }
            }

        }
    }

    private fun addOrEditCustomer(customerId: String? =null){
        val customerNameResult = validationRepository.validateCustomerName(addEditCustomerState.customerName)
        val customerPhoneResult = validationRepository.validateCustomerPhone(addEditCustomerState.customerPhone, customerId)
        val customerEmailResult = validationRepository.validateCustomerEmail(addEditCustomerState.customerEmail)

        val hasError = listOf(customerNameResult,customerPhoneResult,customerEmailResult).any{
            !it.successful
        }

        if (hasError){
            addEditCustomerState = addEditCustomerState.copy(
                customerPhoneError = customerPhoneResult.errorMessage,
                customerEmailError = customerEmailResult.errorMessage,
                customerNameError = customerNameResult.errorMessage,
            )
            return
        }else{
            viewModelScope.launch {
                val customer = Customer()
                customer.customerName = addEditCustomerState.customerName
                customer.customerEmail = addEditCustomerState.customerEmail
                customer.customerPhone = addEditCustomerState.customerPhone


                if(customerId.isNullOrEmpty()){
                    when(val result = customerRepository.createNewCustomer(customer)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Customer Created Successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to create new customer"))
                        }
                    }
                }else{
                    val result = customerRepository.updateCustomer(
                        customer,
                        customerId
                    )

                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(
                                UiEvent.Success(
                                successMessage = "Customer Updated Successfully"
                            ))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error( result.message ?: "Unable to update new customer"))
                        }
                    }
                }

                addEditCustomerState = AddEditCustomerState()
            }
        }
    }

    private fun getAllCustomers(searchText:String = "") {
        getAllCustomers.invoke(searchText).onEach { result ->
            when(result){
                is Resource.Loading -> {
                    _customers.value = _customers.value.copy(
                        isLoading = result.isLoading
                    )
                }
                is Resource.Success -> {
                    result.data?.let { customers ->
                        _customers.value = _customers.value.copy(
                            customers = customers,
                        )
                    }
                }
                is Resource.Error -> {
                    _customers.value = _customers.value.copy(
                        error = result.message
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}
