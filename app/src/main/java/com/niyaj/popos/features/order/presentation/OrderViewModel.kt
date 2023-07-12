package com.niyaj.popos.features.order.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.features.order.domain.use_cases.OrderUseCases
import com.niyaj.popos.utils.Constants
import com.niyaj.popos.utils.getCalculatedEndDate
import com.niyaj.popos.utils.getCalculatedStartDate
import com.niyaj.popos.utils.toFormattedDate
import com.niyaj.popos.utils.toTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val orderUseCases : OrderUseCases,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter

    private val _dineInOrders = MutableStateFlow(DineInOrderState())
    val dineInOrders = _dineInOrders.asStateFlow()

    private val _dineOutOrders = MutableStateFlow(DineOutOrderState())
    val dineOutOrders = _dineOutOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _selectedDate = mutableStateOf("")
    val selectedDate: State<String> = _selectedDate
    
    private val getStartDate = derivedStateOf {
        getCalculatedStartDate(date = _selectedDate.value.ifEmpty { LocalDate.now().toString() })
    }

    private val getEndDate = derivedStateOf {
        getCalculatedEndDate(date = _selectedDate.value.ifEmpty { LocalDate.now().toString() })
    }

    init {
        savedStateHandle.get<String>("selectedDate")?.let { selectedDate ->
            if(selectedDate.isNotEmpty() && selectedDate != LocalDate.now().toString()){
                onOrderEvent(OrderEvent.SelectDate(selectedDate))
            }else{
                getAllDineInOrders()
                getAllDineOutOrders()
            }
        }
    }

    fun onOrderEvent(event: OrderEvent){
        when (event){
            is OrderEvent.MarkedAsDelivered -> {
                viewModelScope.launch {
                    orderRepository.updateOrderStatus(event.cartOrderId, OrderStatus.Delivered.orderStatus)
                }
            }

            is OrderEvent.MarkedAsProcessing -> {
                    viewModelScope.launch {
                        when(val result = orderRepository.updateOrderStatus(event.cartOrderId, OrderStatus.Processing.orderStatus)){
                            is Resource.Loading -> {

                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.Success("Order Marked As Processing"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.Error(result.message ?: "Unable To Mark Order As Processing"))
                            }
                        }
                    }
            }

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    when(val result = orderRepository.deleteOrder(event.cartOrderId)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Order Has Been Deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable To Delete Order"))
                        }
                    }
                }
            }

            is OrderEvent.OnSearchOrder -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllDineInOrders(event.searchText)
                    getAllDineOutOrders(event.searchText)
                }
            }

            is OrderEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.value = event.date
                    getAllDineInOrders()
                    getAllDineOutOrders()
                }
            }

            is OrderEvent.PrintDeliveryReport -> {
                printDeliveryReport()
            }

            is OrderEvent.RefreshOrder -> {
                _selectedDate.value = ""
                getAllDineInOrders()
                getAllDineOutOrders()
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
            getAllDineInOrders()
            getAllDineOutOrders()
        }
    }

    private fun connectPrinter(): Boolean {
        return try {
            escposPrinter = EscPosPrinter(
                BluetoothPrintersConnections.selectFirstPaired(),
                Constants.PRINTER_DPI,
                Constants.PRINTER_WIDTH_MM,
                Constants.PRINTER_NBR_LINE
            )

            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    private fun printDeliveryReport(){
        try {
            connectPrinter()

            var printItems = ""

            printItems += getPrintableHeader()
            printItems += getPrintableOrders()

            escposPrinter.printFormattedText(printItems, 50)
        }catch (e: Exception){
            Timber.e(e)
        }
    }

    private fun getPrintableHeader(): String {
        var header = "[C]<b><font size='big'>DELIVERY</font></b>\n\n"

        header += if (selectedDate.value.isEmpty()) {
            "[C]--------- ${System.currentTimeMillis().toString().toFormattedDate} --------\n"
        } else {
            "[C]----------${_selectedDate.value.toFormattedDate}---------\n"
        }

        header += "[L]\n"

        return header
    }

    private fun getPrintableOrders(): String {
        var order = ""

        val dineOutOrders = _dineOutOrders.value.dineOutOrders.asReversed()

        if(dineOutOrders.isNotEmpty()){
            order += "[L]ID[C]Address[R]Time[R]Price\n"
            order += "[L]-------------------------------\n"

            dineOutOrders.forEach { cart ->
                order += "[L]${cart.orderId.takeLast(3)}[C]${cart.customerAddress}[R]${cart.updatedAt.toTime}[R]${cart.totalAmount}\n"
                order += "[L]-------------------------------\n"
            }
        }else {
            order += "[C]You have not place any order.\n"
        }

        order += "[L]\n"

        return order
    }

    private fun getAllDineOutOrders(
        searchText : String = "",
        startDate : String = getStartDate.value,
        endDate : String = getEndDate.value,
    ) {
        viewModelScope.launch {
            orderUseCases.getAllDineOutOrders(searchText, startDate, endDate).collectLatest { result ->
                when(result){
                    is Resource.Loading -> {
                        _dineOutOrders.value = _dineOutOrders.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _dineOutOrders.value = _dineOutOrders.value.copy(dineOutOrders = it)
                        }
                    }
                    is Resource.Error -> {
                        _dineOutOrders.value = _dineOutOrders.value.copy(error = result.message)
                    }
                }
            }
        }
    }

    private fun getAllDineInOrders(
        searchText : String = "",
        startDate : String = getStartDate.value,
        endDate : String = getEndDate.value,
    ) {
        viewModelScope.launch {
            orderUseCases.getAllDineInOrders(searchText, startDate, endDate).collectLatest { result ->
                when(result){
                    is Resource.Loading -> {
                        _dineInOrders.value = _dineInOrders.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _dineInOrders.value = _dineInOrders.value.copy(dineInOrders = it)
                        }
                    }
                    is Resource.Error -> {
                        _dineInOrders.value = _dineInOrders.value.copy(error = result.message)
                    }
                }
            }
        }
    }
}