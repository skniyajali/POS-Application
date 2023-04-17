package com.niyaj.popos.features.order.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.order.domain.use_cases.OrderUseCases
import com.niyaj.popos.features.order.domain.util.FilterOrder
import com.niyaj.popos.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderUseCases: OrderUseCases,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private lateinit var escposPrinter: EscPosPrinter

    private val _orders = MutableStateFlow(OrderState())
    val orders = _orders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate = _selectedDate.asStateFlow()

    private var getStartDate = getCalculatedStartDate(date = _selectedDate.value.ifEmpty { getStartTime })
    private var getEndDate = getCalculatedEndDate(date = _selectedDate.value.ifEmpty { getStartTime })


    init {
        savedStateHandle.get<String>("selectedDate")?.let { selectedDate ->
            if(selectedDate.isNotEmpty() && selectedDate != LocalDate.now().toString()){
                onOrderEvent(OrderEvent.SelectDate(selectedDate))
            }else{
                getAllOrders()
            }
        }
    }

    fun onOrderEvent(event: OrderEvent){
        when (event){
            is OrderEvent.MarkedAsDelivered -> {
                viewModelScope.launch {
                    orderUseCases.changeOrderStatus(event.cartOrderId, OrderStatus.Delivered.orderStatus)
                    getAllOrders(FilterOrder.ByUpdatedDate(SortType.Descending))
                }
            }

            is OrderEvent.MarkedAsProcessing -> {
                    viewModelScope.launch {
                        when(val result = orderUseCases.changeOrderStatus(event.cartOrderId, OrderStatus.Processing.orderStatus)){
                            is Resource.Loading -> {

                            }
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Order Marked As Processing"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable To Mark Order As Processing"))
                            }
                        }
                    }
            }

            is OrderEvent.DeleteOrder -> {
                viewModelScope.launch {
                    if(event.cartOrderId.isNotEmpty()){
                        when(val result = orderUseCases.deleteOrder(event.cartOrderId)){
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                _eventFlow.emit(UiEvent.OnSuccess("Order Has Been Deleted successfully"))
                            }
                            is Resource.Error -> {
                                _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable To Delete Order"))
                            }
                        }
                    }else{
                        _eventFlow.emit(UiEvent.OnError("Unable To Delete Order"))
                    }
                }
            }

            is OrderEvent.OnFilterOrder -> {
                if(_orders.value.filterOrder::class == event.filterOrder::class &&
                   _orders.value.filterOrder.sortType == event.filterOrder.sortType
                ){
                    _orders.value = _orders.value.copy(
                        filterOrder = FilterOrder.ByUpdatedDate(SortType.Descending),
                    )
                    return
                }

                _orders.value = _orders.value.copy(
                    filterOrder = event.filterOrder,
                )
                getAllOrders(event.filterOrder)
            }

            is OrderEvent.OnSearchOrder -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)
                    getAllOrders(
                        FilterOrder.ByUpdatedDate(SortType.Descending),
                        event.searchText
                    )
                }
            }

            is OrderEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is OrderEvent.SelectDate -> {
                viewModelScope.launch {
                    getStartDate = getCalculatedStartDate(date = event.date)
                    getEndDate = getCalculatedEndDate(date = event.date)

                    getAllOrders()
                    _selectedDate.emit(getStartDate)
                }
            }

            is OrderEvent.PrintDeliveryReport -> {
                printDeliveryReport()
            }

            is OrderEvent.RefreshOrder -> {
                getAllOrders()
                _selectedDate.value = ""
            }
        }
    }

    private fun getAllOrders(
        filterOrder: FilterOrder = _orders.value.filterOrder,
        searchText: String = "",
        startDate: String = getStartDate,
        endDate: String = getEndDate,
    ){
        viewModelScope.launch(Dispatchers.IO) {
            orderUseCases.getAllOrders(filterOrder, searchText, startDate, endDate).collectLatest { result ->
                when(result){
                    is Resource.Loading -> {
                        _orders.value = _orders.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _orders.value = _orders.value.copy(
                                orders = it,
                                filterOrder = filterOrder,
                            )
                        }
                    }
                    is Resource.Error -> {
                        _orders.value = _orders.value.copy(
                            error =  result.message
                        )
                    }
                }
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
            getAllOrders(
                FilterOrder.ByUpdatedDate(SortType.Descending),
                _searchText.value
            )
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

        val dineOutOrders = _orders.value.orders.filter { cart ->
            cart.cartOrder?.orderType == CartOrderType.DineOut.orderType
        }.asReversed()

        if(dineOutOrders.isNotEmpty()){
            order += "[L]ID[C]Address[R]Time[R]Price\n"
            order += "[L]-------------------------------\n"

            dineOutOrders.forEach { cart ->
                if (cart.cartOrder != null){
                    order += "[L]${cart.cartOrder.orderId.takeLast(3)}[C]${cart.cartOrder.address?.shortName}[R]${cart.cartOrder.updatedAt?.toTime}[R]${cart.orderPrice.first.minus(cart.orderPrice.second)}\n"
                    order += "[L]-------------------------------\n"
                }
            }
        }else {
            order += "[C]You have not place any order.\n"
        }

        order += "[L]\n"

        return order
    }

}